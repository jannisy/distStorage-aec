package de.tuberlin.aec.communication;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import de.tub.ise.hermes.IRequestHandler;
import de.tub.ise.hermes.Request;
import de.tub.ise.hermes.Response;
import de.tuberlin.aec.DistoNodeApi;
import de.tuberlin.aec.PendingRequest;
import de.tuberlin.aec.message.WriteSuggestionMessage;
import de.tuberlin.aec.storage.LocalStorage;
import de.tuberlin.aec.util.NetworkConfiguration;
import de.tuberlin.aec.util.NodeConfiguration;
import de.tuberlin.aec.util.PathConfiguration;
import de.tuberlin.aec.util.PathLink;

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
			List<PathLink> syncPaths = pathConfig.getSyncNodePathLinks(startNode, nodeConfig.getHostAndPort());
			List<PathLink> asyncPaths = pathConfig.getAsyncNodePathLinks(startNode, nodeConfig.getHostAndPort());
			final PendingRequest pendingRequest;
			if(!syncPaths.isEmpty()) {
				pendingRequest = new PendingRequest(startNode, key, value, msg.getExpectResponse());
				pendingRequest.setResponseNode(originator);
				pendingRequest.addNodesToNecessaryResponses(allNeighbours);
				localStorage.setPendingRequest(key, pendingRequest);
				localStorage.lock(key);
			} else {
				pendingRequest = null;
				// no neighbours or only async
				// -> reply with ACK immediately
				boolean ack = true;
				if(true) {
					// commit immediately
					localStorage.put(key, value);
				} else {
					// lock and expect commit later?
					localStorage.lock(key); 
				}
				if(msg.getExpectResponse()) {
					msgSender.sendSyncWriteSuggestionResponse(originator.getHostName(), originator.getPort(), key, ack);
				}
			}
			for(PathLink syncPath : syncPaths) {
				InetSocketAddress address = NetworkConfiguration.createAddressFromString(syncPath.getTarget());
				boolean expectResponse = true;
				msgSender.sendWriteSuggestion(address.getHostName(), address.getPort(), key, value, startNode, expectResponse);
			}
			for(PathLink asyncPath : asyncPaths) {
				InetSocketAddress address = NetworkConfiguration.createAddressFromString(asyncPath.getTarget());
				boolean expectResponse = false;
				msgSender.sendWriteSuggestion(address.getHostName(), address.getPort(), key, value, startNode, expectResponse);
			}

			if(!syncPaths.isEmpty()) {

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

	@Override
	public boolean requiresResponse() {
		return true;
	}

}
