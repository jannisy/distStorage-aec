package de.tuberlin.aec.message;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

import de.tub.ise.hermes.Request;
import de.tuberlin.aec.DistoNode;

public class WriteCommitMessage extends DistoMessage {
	
	public static WriteCommitMessage createFromRequest(Request request) {
		WriteCommitMessage msg = new WriteCommitMessage(request.getItems(), request.getOriginator());
		return msg;
	}
	private WriteCommitMessage(List<Serializable> items, String sender) {
		super(items, DistoNode.HANDLER_SYNC_WRITE_COMMIT, sender);
	}
	public WriteCommitMessage(String key, String value, String sender) {
		super(DistoNode.HANDLER_SYNC_WRITE_COMMIT, sender);
		setKey(key);
		setValue(value);
	}
	
	private String setKey(String key) {
		HashMap<String, String> map = getRequestMap();
		return map.put("key", key);
	}
	public String getKey() {
		HashMap<String, String> map = getRequestMap();
		return map.get("key");
	}

	private String setValue(String value) {
		HashMap<String, String> map = getRequestMap();
		return map.put("value", value);
	}
	public String getValue() {
		HashMap<String, String> map = getRequestMap();
		return map.get("value");
	}

}
