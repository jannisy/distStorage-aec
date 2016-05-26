package de.tuberlin.aec.communication;

import de.tub.ise.hermes.IRequestHandler;
import de.tub.ise.hermes.Request;
import de.tub.ise.hermes.Response;
import de.tuberlin.aec.storage.LocalStorage;
import de.tuberlin.aec.util.NodeConfiguration;
import de.tuberlin.aec.util.PathConfiguration;

public class SyncWriteSuggestionResponseHandler implements IRequestHandler {

	private LocalStorage localStorage;
	private PathConfiguration pathConfig;
	private MessageSender msgSender;
	private NodeConfiguration nodeConfig;

	public SyncWriteSuggestionResponseHandler(LocalStorage localStorage, PathConfiguration pathConfig, NodeConfiguration nodeConfig, MessageSender msgSender) {
		this.localStorage = localStorage;
		this.pathConfig = pathConfig;
		this.msgSender = msgSender;
		this.nodeConfig = nodeConfig;
	}
	@Override
	public Response handleRequest(Request request) {
		System.out.println("Received SyncWriteSuggestionResponse Message.");
		Response response = new Response("", true, request, "");
		return response;
	}

	@Override
	public boolean requiresResponse() {
		return true;
	}

}
