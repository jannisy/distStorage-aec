package de.tuberlin.aec.communication;

import de.tub.ise.hermes.IRequestHandler;
import de.tub.ise.hermes.Request;
import de.tub.ise.hermes.Response;
import de.tuberlin.aec.message.SyncWriteCommitMessage;
import de.tuberlin.aec.storage.LocalStorage;

public class SyncWriteCommitHandler implements IRequestHandler {

	private LocalStorage localStorage;

	public SyncWriteCommitHandler(LocalStorage localStorage) {
		this.localStorage = localStorage;
	}
	@Override
	public Response handleRequest(Request request) {
		SyncWriteCommitMessage msg = SyncWriteCommitMessage.createFromRequest(request);
		
		System.out.println("Received SyncWriteCommit Message with key=" + msg.getKey());
		String keyToCommit = msg.getKey();
		assert(localStorage.isLocked(keyToCommit));
		localStorage.unlock(keyToCommit);
		
		Response response = new Response("", true, request, "");
		return response;
	}

	@Override
	public boolean requiresResponse() {
		return true;
	}

}
