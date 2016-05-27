package de.tuberlin.aec.message;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

import de.tub.ise.hermes.Request;
import de.tuberlin.aec.DistoNode;

public class WriteSuggestionMessage extends DistoMessage {

	public WriteSuggestionMessage(String sender, String key, String value, String startNode, boolean expectResponse) {
		super(DistoNode.HANDLER_SYNC_WRITE_SUGGESTION, sender);
		setKey(key);
		setValue(value);
		setStartNode(startNode);
		setExpectResponse(expectResponse);
	}
	public static WriteSuggestionMessage createFromRequest(Request request) {
		WriteSuggestionMessage msg = new WriteSuggestionMessage(request.getItems(), request.getOriginator());
		return msg;
	}
	private WriteSuggestionMessage(List<Serializable> items, String sender) {
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
	private void setExpectResponse(boolean expectResponse) {
		HashMap<String, String> map = getRequestMap();
		String stringAck = expectResponse ? "1" : "0";
		map.put("expectResponse", stringAck);
	}
	
	public boolean getExpectResponse() {
		HashMap<String, String> map = getRequestMap();
		if(map.containsKey("expectResponse") && map.get("expectResponse").equals("1")) {
			return true;
		} else {
			return false;
		}
		
	}
	

}
