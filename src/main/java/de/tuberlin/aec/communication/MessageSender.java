package de.tuberlin.aec.communication;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import de.tub.ise.hermes.Request;
import de.tub.ise.hermes.Sender;
import de.tuberlin.aec.message.WriteCommitMessage;
import de.tuberlin.aec.message.WriteSuggestionMessage;
import de.tuberlin.aec.message.WriteSuggestionResponseMessage;

/**
 * Created by joh-mue on 24/05/16.
 */
public class MessageSender {
	
	private String sender;

	public MessageSender(String sender) {
		this.sender = sender;
	}
    private void sendMessage(String host, int port, Request request) {
	    
	    ScheduledThreadPoolExecutor exec = new ScheduledThreadPoolExecutor(1);

	    exec.schedule(new Runnable() {
	              public void run() {
	                  Sender sender = new Sender(host, port);
	                  sender.sendMessage(request, 2000);
	                  //System.out.println("Message sent to " + host + ":" + port);
	              }
	         }, 500, TimeUnit.MILLISECONDS);
	    
	    
    }

    public void sendSyncWriteCommitMessage(String host, int port, String key, String value) {
        WriteCommitMessage msg = new WriteCommitMessage(key, value, sender);
        System.out.println("Send SyncWriteCommitMsg... host=" + host + ":" + port + ", key=" + key);
        sendMessage(host, port, msg);
    }

    public void sendWriteSuggestion(String host, int port, String key, String value, String startNode, boolean expectResponse) {
        System.out.println("Send SyncWriteSuggestion host=" + host + ":" + port + ", key=" + key + ", startNode=" + startNode);
        WriteSuggestionMessage msg = new WriteSuggestionMessage(sender, key, value, startNode, expectResponse);
        sendMessage(host, port, msg);
    }

    public void sendSyncWriteSuggestionResponse(String host, int port, String key, boolean ack) {
        System.out.println("Send SyncWriteSuggResponse host=" + host + ":" + port + ", key=" + key + ", ack=" + ack);
        WriteSuggestionResponseMessage msg = new WriteSuggestionResponseMessage(key, ack, sender);
        sendMessage(host, port, msg);
    }
}
