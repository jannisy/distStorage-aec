package de.tuberlin.aec;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

/**
 * A pending request represents a PUT request which is not yet complete.
 * This class contains all the necessary information on the request.
 * 
 * If a message arrives, an instance of this class is retrieved from the
 * local storage to work continue with the request.
 *
 */
public class PendingRequest {
	
	private String startNode;
	private String key;

	private String value;
	
	private List<InetSocketAddress> necessaryResponses;

	/**
	 * 
	 * @param startNode
	 * @param key
	 * @param value
	 */
	public PendingRequest(String startNode, String key, String value) {
		this.startNode = startNode;
		this.key = key;
		this.value = value;
		
		this.necessaryResponses = new ArrayList<InetSocketAddress>();
	}
	
	/**
	 * Adds a node to the necessary responses list.
	 * The necessary responses list is a list with host names from all
	 * nodes which we require a response from.
	 */
	public void addNodeToNecessaryResponses(InetSocketAddress a) {
		this.necessaryResponses.add(a);
	}
	
	/**
	 * Removes a node from necessary responses list.
	 * The necessary responses list is a list with host names from all
	 * nodes which we require a response from.
	 */
	public void removeNodeFromNecessaryResponses(InetSocketAddress a) {
		this.necessaryResponses.remove(a);
	}
	
	/**
	 * return true if there are responses pending
	 * @return true if there are responses pending
	 */
	public boolean responsesPending() {
		return !necessaryResponses.isEmpty();
	}
	public String getStartNode() {
		return startNode;
	}

	public void setStartNode(String startNode) {
		this.startNode = startNode;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

}
