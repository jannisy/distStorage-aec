package de.tuberlin.aec.communication;

import de.tub.ise.hermes.IRequestHandler;
import de.tub.ise.hermes.Request;
import de.tub.ise.hermes.Response;
import de.tuberlin.aec.message.DeleteRequestMessage;
import de.tuberlin.aec.message.WriteCommitMessage;
import de.tuberlin.aec.storage.LocalStorage;
import de.tuberlin.aec.util.NodeConfiguration;
import de.tuberlin.aec.util.PathConfiguration;

/**
 * This class handles an incoming delete request. 
 */
public class DeleteRequestHandler implements IRequestHandler {

	private LocalStorage localStorage;

	public DeleteRequestHandler(LocalStorage localStorage) {
		this.localStorage = localStorage;
	}
	@Override
	public Response handleRequest(Request request) {
		DeleteRequestMessage msg = DeleteRequestMessage.createFromRequest(request);
		
		System.out.println("Received DeleteRequest key=" + msg.getKey());
		String key = msg.getKey();

		localStorage.unlock(key);
		localStorage.delete(key);
		
		Response response = new Response("", true, request, "");
		return response;
	}

	@Override
	public boolean requiresResponse() {
		return true;
	}

}
