package de.tuberlin.aec.message;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

import de.tub.ise.hermes.Request;
import de.tuberlin.aec.DistoNode;

public class SyncWriteCommitMessage extends DistoMessage {

	public SyncWriteCommitMessage(String key, String sender) {
		super(DistoNode.HANDLER_SYNC_WRITE_COMMIT, sender);
		setKey(key);
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
