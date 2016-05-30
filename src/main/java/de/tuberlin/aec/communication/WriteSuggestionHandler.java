package de.tuberlin.aec.communication;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import de.tub.ise.hermes.IRequestHandler;
import de.tub.ise.hermes.Request;
import de.tub.ise.hermes.Response;
import de.tuberlin.aec.DistoNodeApi;
import de.tuberlin.aec.PendingQuorum;
import de.tuberlin.aec.PendingRequest;
import de.tuberlin.aec.message.WriteSuggestionMessage;
import de.tuberlin.aec.storage.LocalStorage;
import de.tuberlin.aec.util.NetworkConfiguration;
import de.tuberlin.aec.util.NodeConfiguration;
import de.tuberlin.aec.util.PathConfiguration;
import de.tuberlin.aec.util.PathLink;


/**
 * This class handles an incoming WriteSuggestionMessage
 */
public class WriteSuggestionHandler implements IRequestHandler {

	private LocalStorage localStorage;
	private PathConfiguration pathConfig;
	private NodeConfiguration nodeConfig;
	private MessageSender msgSender;

	public WriteSuggestionHandler(LocalStorage localStorage, PathConfiguration pathConfig, NodeConfiguration nodeConfig, MessageSender msgSender) {
		this.localStorage = localStorage;
		this.pathConfig = pathConfig;
		this.nodeConfig = nodeConfig;
		this.msgSender = msgSender;
	}

	@Override
	public Response handleRequest(Request request) {
		System.out.println("Received SyncWriteSuggestion Message.");
		WriteSuggestionMessage msg = WriteSuggestionMessage.createFromRequest(request);

		String startNode = msg.getStartNode();
		String key = msg.getKey();
		String value = msg.getValue();

		InetSocketAddress originator = NetworkConfiguration.createAddressFromString(request.getOriginator());
		
		if(localStorage.isLocked(key)) {
			boolean ack = false;
			msgSender.sendSyncWriteSuggestionResponse(originator.getHostName(), originator.getPort(), key, ack);
		} else {
			List<String> allNeighbours = pathConfig.getNodeNeighbours(startNode, nodeConfig.getHostAndPort());
			List<String> syncNeighbours = pathConfig.getSyncNeighbours(startNode, nodeConfig.getHostAndPort());
			List<PathLink> syncPaths = pathConfig.getSyncNodePathLinks(startNode, nodeConfig.getHostAndPort());
			List<PathLink> asyncPaths = pathConfig.getAsyncNodePathLinks(startNode, nodeConfig.getHostAndPort());
			List<PathLink> quorum = pathConfig.getQuorumPathLinks(startNode, nodeConfig.getHostAndPort());

			final PendingRequest pendingRequest;

			if (!quorum.isEmpty()) {
				// start a quorum
				pendingRequest = new PendingQuorum(startNode, key, value, msg.getExpectResponse(), pathConfig.getQuorumSize(startNode));
				pendingRequest.addNodesToNecessaryResponses(allNeighbours);
				pendingRequest.setResponseNode(originator);
				localStorage.setPendingRequest(key, pendingRequest);
				localStorage.lock(key);
			} else {
				if (!syncPaths.isEmpty()) {
					if (localStorage.getPendingRequest(key) != null) {
						System.out.println("Internal Error: There is a pending request on this key.");
					}
					pendingRequest = new PendingRequest(startNode, key, value, msg.getExpectResponse());
					pendingRequest.addNodesToNecessaryResponses(syncNeighbours);
					pendingRequest.setResponseNode(originator);
					localStorage.setPendingRequest(key, pendingRequest);
					localStorage.lock(key);
				} else {
					pendingRequest = null;
					if(msg.getExpectResponse()) { // sync message, lock and wait for commitMessage
						localStorage.lock(key);
						boolean ack = true;
						msgSender.sendSyncWriteSuggestionResponse(originator.getHostName(), originator.getPort(), key, ack);
					} else { // commit right away
						localStorage.put(key, value);
					}
				}
			}

			sendWriteSuggestions(syncPaths, key, value, startNode, true);
			sendWriteSuggestions(asyncPaths, key, value, startNode, false);
			sendWriteSuggestions(quorum, key, value, startNode, true);

			if(!syncPaths.isEmpty() || !quorum.isEmpty()) {

			    ScheduledThreadPoolExecutor exec = new ScheduledThreadPoolExecutor(1);
			    // abort after timeout!
			    exec.schedule(new Runnable() {
			              public void run() {
			            	  if(!pendingRequest.isFinished()) {
				            	  System.out.println("Timeout for Put Request key=" + key);
				            	  localStorage.unlock(key);
				            	  localStorage.removePendingRequest(key);
			            	  }
			              }
			         }, DistoNodeApi.REQUEST_TIMEOUT_MS, TimeUnit.MILLISECONDS);
			}
		}
		Response response = new Response("", true, request, "");
		return response;
	}

	private void sendWriteSuggestions(List<PathLink> paths, String key, String value, String startNode, boolean expectResponse) {
		for (PathLink path : paths) {
			InetSocketAddress address = NetworkConfiguration.createAddressFromString(path.getTarget());
			msgSender.sendWriteSuggestion(address.getHostName(), address.getPort(), key, value, startNode, expectResponse);
		}
	}

	@Override
	public boolean requiresResponse() {
		return true;
	}

}
