package de.tuberlin.aec;

import de.tuberlin.aec.communication.MessageSender;
import de.tuberlin.aec.storage.LocalStorage;

/**
 * This class receives requests for a disto node.
 * Its methods are called by the REST API.
 * 
 *
 */
public class DistoNodeApi {

	private LocalStorage localStorage;
	private MessageSender msgSender;

	public DistoNodeApi(LocalStorage localStorage, MessageSender msgSender) {
		this.localStorage = localStorage;
		this.msgSender = msgSender;
		
	}

	public void put(String key, String value) {
	}
	
	public String get(String key) {
		return "";
	}
	
	public void delete(String key) {
		
	}
}
