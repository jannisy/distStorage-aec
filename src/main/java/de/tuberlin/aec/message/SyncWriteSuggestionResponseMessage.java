package de.tuberlin.aec.message;

import java.util.HashMap;

import de.tuberlin.aec.DistoNode;

public class SyncWriteSuggestionResponseMessage extends DistoMessage {

	public SyncWriteSuggestionResponseMessage(boolean ack, String sender) {
		super(DistoNode.HANDLER_SYNC_WRITE_SUGGESTION_RESPONSE, sender);
	}
	
	public void setResponse(boolean ack) {
		HashMap<String, String> map = getRequestMap();
		String stringAck = ack ? "1" : "0";
		map.put("key", stringAck);
	}

	public boolean getResponse() {
		HashMap<String, String> map = getRequestMap();
		String stringAck = map.get("key");
		if(stringAck.equals("1")) {
			return true;
		} else if(stringAck.equals("0")) {
			return false;
		} else {
			// TODO internal error
			return false;
		}
	}
}
