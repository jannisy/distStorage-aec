package de.tuberlin.aec.message;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

import de.tub.ise.hermes.Request;
import de.tuberlin.aec.DistoNode;

public class SyncWriteSuggestionMessage extends DistoMessage {

	public SyncWriteSuggestionMessage(String sender, String key, String value, String startNode) {
		super(DistoNode.HANDLER_SYNC_WRITE_SUGGESTION, sender);
		setKey(key);
		setValue(value);
		setStartNode(startNode);
	}
	public static SyncWriteSuggestionMessage createFromRequest(Request request) {
		SyncWriteSuggestionMessage msg = new SyncWriteSuggestionMessage(request.getItems(), request.getOriginator());
		return msg;
	}
	private SyncWriteSuggestionMessage(List<Serializable> items, String sender) {
		super(items, DistoNode.HANDLER_SYNC_WRITE_COMMIT, sender);
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

	private String setStartNode(String value) {
		HashMap<String, String> map = getRequestMap();
		return map.put("startNode", value);
	}
	public String getStartNode() {
		HashMap<String, String> map = getRequestMap();
		return map.get("startNode");
	}
	

}
