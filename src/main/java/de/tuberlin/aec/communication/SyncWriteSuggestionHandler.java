package de.tuberlin.aec.communication;

import de.tub.ise.hermes.IRequestHandler;
import de.tub.ise.hermes.Request;
import de.tub.ise.hermes.Response;
import de.tuberlin.aec.storage.LocalStorage;

public class SyncWriteSuggestionHandler implements IRequestHandler {

	private LocalStorage localStorage;

	public SyncWriteSuggestionHandler(LocalStorage localStorage) {
		this.localStorage = localStorage;
	}

	@Override
	public Response handleRequest(Request request) {
		// TODO
		System.out.println("Received SyncWriteSuggestion Message.");
		Response response = new Response("", true, request, "");
		return response;
	}

	@Override
	public boolean requiresResponse() {
		return true;
	}

}
