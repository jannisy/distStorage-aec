package de.tuberlin.aec.communication;

import java.net.InetSocketAddress;

import de.tub.ise.hermes.IRequestHandler;
import de.tub.ise.hermes.Request;
import de.tub.ise.hermes.Response;
import de.tuberlin.aec.PendingRequest;
import de.tuberlin.aec.message.WriteSuggestionResponseMessage;
import de.tuberlin.aec.storage.LocalStorage;
import de.tuberlin.aec.util.NetworkConfiguration;
import de.tuberlin.aec.util.NodeConfiguration;
import de.tuberlin.aec.util.PathConfiguration;

public class WriteSuggestionResponseHandler implements IRequestHandler {

	private LocalStorage localStorage;
	private PathConfiguration pathConfig;
	private MessageSender msgSender;
	private NodeConfiguration nodeConfig;
	private NetworkConfiguration networkConfig;

	public WriteSuggestionResponseHandler(LocalStorage localStorage, PathConfiguration pathConfig,
			NodeConfiguration nodeConfig, NetworkConfiguration networkConfig, MessageSender msgSender) {
		this.localStorage = localStorage;
		this.pathConfig = pathConfig;
		this.msgSender = msgSender;
		this.nodeConfig = nodeConfig;
		this.networkConfig = networkConfig;
	}

	@Override
	public Response handleRequest(Request request) {
		System.out.println("Received SyncWriteSuggestionResponse Message.");
		WriteSuggestionResponseMessage msg = WriteSuggestionResponseMessage.createFromRequest(request);
		String key = msg.getKey();
		// TODO: handle the case where this node is not the start node -> send response back 
		PendingRequest pendingRequest = localStorage.getPendingRequest(key);
		if (pendingRequest == null) {
			// TODO internal error
			System.out.println("Internal Error: Could not find pending request.");
		} else {
			if(pendingRequest.getStartNode().equals(nodeConfig.getHostAndPort())) {
				// This is the node where the original request was made
				handleRequestStartNode(msg, key, request, pendingRequest);
			} else {
				// This is an intermediary node
				handleRequestIntermediaryNode(msg, key, request, pendingRequest);
			}
		}

		Response response = new Response("", true, request, "");
		return response;
	}
	private void handleRequestIntermediaryNode(WriteSuggestionResponseMessage msg, String key, Request request, PendingRequest pendingRequest) {

		boolean ack = msg.getResponse();
		if(ack) {
			InetSocketAddress address = NetworkConfiguration.createAddressFromString(request.getOriginator());
			pendingRequest.removeNodeFromNecessaryResponses(address);
			
			if(!pendingRequest.responsesPending()) {
				if(pendingRequest.getExpectResponse()) {
					boolean responseAck = true;
					msgSender.sendSyncWriteSuggestionResponse(pendingRequest.getResponseNode().getHostName(), 
							pendingRequest.getResponseNode().getPort(), key, responseAck);
				}
			} else {
				System.out.println("  Awaiting further response messages.");
			}
		} else {
			if(pendingRequest.getExpectResponse()) {
				// Negative Response - Send NACK
				boolean responseAck = false;
				msgSender.sendSyncWriteSuggestionResponse(pendingRequest.getResponseNode().getHostName(), 
						pendingRequest.getResponseNode().getPort(), key, responseAck);
			}
			localStorage.unlock(key);
			localStorage.removePendingRequest(key);
		}
	}
	
	private void handleRequestStartNode(WriteSuggestionResponseMessage msg, String key, Request request, PendingRequest pendingRequest) {

		boolean ack = msg.getResponse();
		if(ack) {
			InetSocketAddress address = NetworkConfiguration.createAddressFromString(request.getOriginator());
			pendingRequest.removeNodeFromNecessaryResponses(address);
			
			if(!pendingRequest.responsesPending()) {
				handleRequestCommit(key, pendingRequest);
			} else {
				System.out.println("  Awaiting further response messages.");
			}
		} else {
			// Negative Response - Abort put
			handleRequestAbort(key, pendingRequest);
		}
	}

	/**
	 * Handles the abort case:
	 * There is a negative WriteSuggestion response -> abort PUT request.
	 * so the new value can be commited.
	 */
	private void handleRequestAbort(String key, PendingRequest pendingRequest) {
		System.out.println("Abort Request: [" + key + ", " + pendingRequest.getValue() + "]");
		localStorage.unlock(key);
		localStorage.removePendingRequest(key);
		
	}
	
	/**
	 * Handles the commit case:
	 * Responses from all nodes have arrived and are positive,
	 * so the new value can be commited.
	 */
	private void handleRequestCommit(String key, PendingRequest pendingRequest) {
		System.out.println("Commit Request: [" + key + ", " + pendingRequest.getValue() + "]");
		localStorage.unlock(key);
		localStorage.removePendingRequest(key);
		localStorage.put(key, pendingRequest.getValue());
		sendCommitMessages(key, pendingRequest.getValue());
	}
	
	private void sendCommitMessages(String key, String value) {
		for(InetSocketAddress node : networkConfig.getAllNodes()) {
			if(node.equals(nodeConfig.getSocket())) {
				// Don't send to this node
				continue;
			}
			msgSender.sendSyncWriteCommitMessage(node.getHostName(), node.getPort(), key, value);
		}
	}

	@Override
	public boolean requiresResponse() {
		return true;
	}

}
