package de.tuberlin.aec.communication;

import java.net.InetSocketAddress;
import java.util.List;

import de.tub.ise.hermes.IRequestHandler;
import de.tub.ise.hermes.Request;
import de.tub.ise.hermes.Response;
import de.tuberlin.aec.PendingRequest;
import de.tuberlin.aec.message.SyncWriteSuggestionMessage;
import de.tuberlin.aec.storage.LocalStorage;
import de.tuberlin.aec.util.NetworkConfiguration;
import de.tuberlin.aec.util.NodeConfiguration;
import de.tuberlin.aec.util.PathConfiguration;

public class SyncWriteSuggestionHandler implements IRequestHandler {

	private LocalStorage localStorage;
	private PathConfiguration pathConfig;
	private NodeConfiguration nodeConfig;
	private MessageSender msgSender;

	public SyncWriteSuggestionHandler(LocalStorage localStorage, PathConfiguration pathConfig, NodeConfiguration nodeConfig, MessageSender msgSender) {
		this.localStorage = localStorage;
		this.pathConfig = pathConfig;
		this.nodeConfig = nodeConfig;
		this.msgSender = msgSender;
	}

	@Override
	public Response handleRequest(Request request) {
		System.out.println("Received SyncWriteSuggestion Message.");
		SyncWriteSuggestionMessage msg = SyncWriteSuggestionMessage.createFromRequest(request);

		String startNode = msg.getStartNode();
		String key = msg.getKey();
		String value = msg.getValue();

		InetSocketAddress originator = NetworkConfiguration.createAddressFromString(request.getOriginator());
		
		if(localStorage.isLocked(key)) {
			boolean ack = false;
			msgSender.sendSyncWriteSuggestionResponse(originator.getHostName(), originator.getPort(), key, ack);
		} else {
			List<String> neighbours = pathConfig.getNodeNeighbours(startNode, nodeConfig.getHostAndPort());
			if(neighbours.isEmpty()) {
				// no neighbours - reply with ACK immediately
				boolean ack = true;
				localStorage.lock(key);
				msgSender.sendSyncWriteSuggestionResponse(originator.getHostName(), originator.getPort(), key, ack);
			} else {
				// TODO add Pending Request
				PendingRequest pendingRequest = new PendingRequest(startNode, key, value);
				pendingRequest.setResponseNode(originator);
				pendingRequest.addNodesToNecessaryResponses(neighbours);
				localStorage.setPendingRequest(key, pendingRequest);
				localStorage.lock(key);
				for(String neighbour : neighbours) {
					InetSocketAddress address = NetworkConfiguration.createAddressFromString(neighbour);
					msgSender.sendSyncWriteSuggestion(address.getHostName(), address.getPort(), key, value, startNode);
				}
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
