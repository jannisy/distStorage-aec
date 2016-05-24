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
		if(request instanceof SyncWriteCommitMessage) {
			SyncWriteCommitMessage msg = (SyncWriteCommitMessage) request;
			String keyToCommit = msg.getKey();
			assert(localStorage.isLocked(keyToCommit));
			localStorage.unlock(keyToCommit);
			
			// TODO stop timeout mechanism for this key? 
		} else {
			// TODO error
		}
		return null;
	}

	@Override
	public boolean requiresResponse() {
		// TODO Auto-generated method stub
		return false;
	}

}
