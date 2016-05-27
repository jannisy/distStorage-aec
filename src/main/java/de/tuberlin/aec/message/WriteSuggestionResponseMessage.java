package de.tuberlin.aec.message;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

import de.tub.ise.hermes.Request;
import de.tuberlin.aec.DistoNode;

public class WriteSuggestionResponseMessage extends DistoMessage {

	public WriteSuggestionResponseMessage(String key, boolean ack, String sender) {
		super(DistoNode.HANDLER_SYNC_WRITE_SUGGESTION_RESPONSE, sender);
		setResponse(ack);
		setKey(key);
	}
	public static WriteSuggestionResponseMessage createFromRequest(Request request) {
		WriteSuggestionResponseMessage msg = new WriteSuggestionResponseMessage(request.getItems(), request.getOriginator());
		return msg;
	}
	private WriteSuggestionResponseMessage(List<Serializable> items, String sender) {
		super(items, DistoNode.HANDLER_SYNC_WRITE_SUGGESTION_RESPONSE, sender);
	}
	
	private void setResponse(boolean ack) {
		HashMap<String, String> map = getRequestMap();
		String stringAck = ack ? "1" : "0";
		map.put("ack", stringAck);
	}
	
	public boolean getResponse() {
		HashMap<String, String> map = getRequestMap();
		if(map.containsKey("ack") && map.get("ack").equals("1")) {
			return true;
		} else {
			return false;
		}
		
	}
	private String setKey(String key) {
		HashMap<String, String> map = getRequestMap();
		return map.put("key", key);
	}
	public String getKey() {
		HashMap<String, String> map = getRequestMap();
		return map.get("key");
	}
}
