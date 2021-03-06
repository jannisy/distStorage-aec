package de.tuberlin.aec;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

import de.tuberlin.aec.communication.PutResponse;
import de.tuberlin.aec.util.NetworkConfiguration;
import de.tuberlin.aec.util.PathLink;

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
	
	private boolean finished = false;
	
	private List<InetSocketAddress> necessaryResponses;
	private InetSocketAddress responseNode;
	private boolean expectResponse;
	private PutResponse response;

	/**
	 * 
	 * @param startNode
	 * @param key
	 * @param value
	 */
	public PendingRequest(String startNode, String key, String value, boolean expectResponse) {
		this.startNode = startNode;
		this.key = key;
		this.value = value;
		this.expectResponse = expectResponse;
		
		this.necessaryResponses = new ArrayList<InetSocketAddress>();
	}

	/**
	 * Adds a node to the necessary responses list.
	 * The necessary responses list is a list with host names from all
	 * nodes which we require a response from.
	 */
	private void addNodeToNecessaryResponses(InetSocketAddress a) {
		this.necessaryResponses.add(a);
	}

	/**
	 * Adds all nodes in the given list to the necessary responses list.
	 * The necessary responses list is a list with host names from all
	 * nodes which we require a response from.
	 */
	public void addNodesToNecessaryResponses(List<String> list) {
		for(String node : list) {
			InetSocketAddress address = NetworkConfiguration.createAddressFromString(node);
			this.addNodeToNecessaryResponses(address);
		}
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
	 * sets the node which should receive the response of the request
	 * @param node the response node
	 */
	public void setResponseNode(InetSocketAddress node) {
		this.responseNode = node;
	}

	/**
	 * returns the node which should receive the response of the request
	 * @return the node which should receive the response of the request
	 */
	public InetSocketAddress getResponseNode() {
		return responseNode;
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

	public boolean getExpectResponse() {
		return expectResponse;
	}

	/**
	 * returns true if the request is finished
	 * @return
	 */
	public boolean isFinished() {
		return finished;
	}

	public void setFinished(boolean finished) {
		this.finished = finished;
	}

	public PutResponse getResponse() {
		return response;
	}

	public void setResponse(PutResponse response) {
		this.response = response;
	}
	
	protected int getNumberOfNecessaryResponses() {
		return necessaryResponses.size();
	}
}
