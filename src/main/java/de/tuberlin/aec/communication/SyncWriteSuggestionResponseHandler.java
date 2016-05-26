package de.tuberlin.aec.communication;

import java.net.InetSocketAddress;

import de.tub.ise.hermes.IRequestHandler;
import de.tub.ise.hermes.Request;
import de.tub.ise.hermes.Response;
import de.tuberlin.aec.PendingRequest;
import de.tuberlin.aec.message.SyncWriteCommitMessage;
import de.tuberlin.aec.message.SyncWriteSuggestionResponseMessage;
import de.tuberlin.aec.storage.LocalStorage;
import de.tuberlin.aec.util.NetworkConfiguration;
import de.tuberlin.aec.util.NodeConfiguration;
import de.tuberlin.aec.util.PathConfiguration;

public class SyncWriteSuggestionResponseHandler implements IRequestHandler {

	private LocalStorage localStorage;
	private PathConfiguration pathConfig;
	private MessageSender msgSender;
	private NodeConfiguration nodeConfig;

	public SyncWriteSuggestionResponseHandler(LocalStorage localStorage, PathConfiguration pathConfig,
			NodeConfiguration nodeConfig, MessageSender msgSender) {
		this.localStorage = localStorage;
		this.pathConfig = pathConfig;
		this.msgSender = msgSender;
		this.nodeConfig = nodeConfig;
	}

	@Override
	public Response handleRequest(Request request) {
		System.out.println("Received SyncWriteSuggestionResponse Message.");
		SyncWriteSuggestionResponseMessage msg = SyncWriteSuggestionResponseMessage.createFromRequest(request);
		String key = msg.getKey();
		boolean ack = msg.getResponse();
		// TODO: handle the case where this node is not the start node -> send response back 
		PendingRequest pendingRequest = localStorage.getPendingRequest(key);
		if (pendingRequest == null) {
			// TODO internal error
			System.out.println("Internal Error: Could not find pending request.");
		} else {
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

		Response response = new Response("", true, request, "");
		return response;
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
	}

	@Override
	public boolean requiresResponse() {
		return true;
	}

}
