package de.tuberlin.aec.communication;

import de.tub.ise.hermes.Request;
import de.tub.ise.hermes.Sender;
import de.tuberlin.aec.message.SyncWriteCommitMessage;
import de.tuberlin.aec.message.SyncWriteSuggestionMessage;
import de.tuberlin.aec.message.SyncWriteSuggestionResponseMessage;

/**
 * Created by joh-mue on 24/05/16.
 */
public class MessageSender {
	
	private String sender;

	public MessageSender(String sender) {
		this.sender = sender;
	}
    private void sendMessage(String host, int port, Request request) {
        Sender sender = new Sender(host, port);
        sender.sendMessage(request, 2000);
        System.out.println("Message sent to " + host + ":" + port);
    }

    public void sendSyncWriteCommitMessage(String host, int port, String key) {
        SyncWriteCommitMessage msg = new SyncWriteCommitMessage(key, sender);
        System.out.println("Send SyncWriteCommitMsg... host=" + host + ":" + port + ", key=" + key);
        sendMessage(host, port, msg);
    }

    public void sendSyncWriteSuggestion(String host, int port, String key, String value, String startNode) {
        SyncWriteSuggestionMessage msg = new SyncWriteSuggestionMessage(sender, key, value, startNode);
        sendMessage(host, port, msg);
    }

    public void sendSyncWriteSuggestionResponse(String host, int port, String key, boolean ack) {
        System.out.println("Send SyncWriteSuggResponse host=" + host + ":" + port + ", key=" + key + ", ack=" + ack);
        SyncWriteSuggestionResponseMessage msg = new SyncWriteSuggestionResponseMessage(key, ack, sender);
        sendMessage(host, port, msg);
    }
}
