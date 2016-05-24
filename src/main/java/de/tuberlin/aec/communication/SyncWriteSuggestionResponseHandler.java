package de.tuberlin.aec.communication;

import de.tub.ise.hermes.IRequestHandler;
import de.tub.ise.hermes.Request;
import de.tub.ise.hermes.Response;
import de.tuberlin.aec.storage.LocalStorage;

public class SyncWriteSuggestionResponseHandler implements IRequestHandler {

	private LocalStorage localStorage;

	public SyncWriteSuggestionResponseHandler(LocalStorage localStorage) {
		this.localStorage = localStorage;
	}
	@Override
	public Response handleRequest(Request arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean requiresResponse() {
		// TODO Auto-generated method stub
		return false;
	}

}
