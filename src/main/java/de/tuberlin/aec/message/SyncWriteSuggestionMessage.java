package de.tuberlin.aec.message;

import de.tub.ise.hermes.Request;
import de.tuberlin.aec.DistoNode;

public class SyncWriteSuggestionMessage extends Request {

	public SyncWriteSuggestionMessage(String sender, String key, String value) {
		super(DistoNode.HANDLER_SYNC_WRITE_SUGGESTION, sender);
	}

}
