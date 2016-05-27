package de.tuberlin.aec.communication;

import de.tub.ise.hermes.IRequestHandler;
import de.tub.ise.hermes.Request;
import de.tub.ise.hermes.Response;
import de.tuberlin.aec.message.WriteCommitMessage;
import de.tuberlin.aec.storage.LocalStorage;
import de.tuberlin.aec.util.NodeConfiguration;
import de.tuberlin.aec.util.PathConfiguration;

public class WriteCommitHandler implements IRequestHandler {

	private LocalStorage localStorage;
	private PathConfiguration pathConfig;
	private MessageSender msgSender;
	private NodeConfiguration nodeConfig;

	public WriteCommitHandler(LocalStorage localStorage, PathConfiguration pathConfig, NodeConfiguration nodeConfig, MessageSender msgSender) {
		this.localStorage = localStorage;
		this.pathConfig = pathConfig;
		this.msgSender = msgSender;
		this.nodeConfig = nodeConfig;
	}
	@Override
	public Response handleRequest(Request request) {
		WriteCommitMessage msg = WriteCommitMessage.createFromRequest(request);
		
		System.out.println("Received SyncWriteCommit Message with key=" + msg.getKey());
		String key = msg.getKey();
		String value = msg.getValue();
		
		assert(localStorage.isLocked(key));
		localStorage.unlock(key);
		localStorage.put(key, value);
		
		Response response = new Response("", true, request, "");
		return response;
	}

	@Override
	public boolean requiresResponse() {
		return true;
	}

}
