package de.tuberlin.aec;

import java.net.InetSocketAddress;
import java.util.List;

import de.tuberlin.aec.communication.MessageSender;
import de.tuberlin.aec.communication.PutResponse;
import de.tuberlin.aec.storage.LocalStorage;
import de.tuberlin.aec.util.NetworkConfiguration;
import de.tuberlin.aec.util.NodeConfiguration;
import de.tuberlin.aec.util.PathConfiguration;
import de.tuberlin.aec.util.PathLink;

/**
 * This class receives requests for a disto node.
 * Its methods are called by the REST API.
 * 
 */
public class DistoNodeApi {

	private LocalStorage localStorage;
	private MessageSender msgSender;
	private NodeConfiguration nodeConfig;
	private PathConfiguration pathConfig;

	public DistoNodeApi(LocalStorage localStorage, MessageSender msgSender, NodeConfiguration nodeConfig, PathConfiguration pathConfig) {
		this.localStorage = localStorage;
		this.msgSender = msgSender;
		this.nodeConfig = nodeConfig;
		this.pathConfig = pathConfig;
	}

	public PutResponse put(String key, String value) {
		System.out.println("# API PUT REQUEST: [" + key + ", " + value + "]");

		if(localStorage.isLocked(key)) {
			System.out.println("Another Put Request is pending. Key is currently locked. Abort request");
			PutResponse response = new PutResponse(false);
			response.setErrorMessage("Pending request on this key.");
			return response;
		} else {
			String startNode = nodeConfig.getHostAndPort();

			List<String> allNeighbours = pathConfig.getNodeNeighbours(startNode, startNode);
			List<PathLink> syncPaths = pathConfig.getSyncNodePathLinks(startNode, startNode);
			List<PathLink> asyncPaths = pathConfig.getAsyncNodePathLinks(startNode, startNode);
			PendingRequest pendingRequest = null;
			if(!syncPaths.isEmpty()) {
				boolean expectResponse = false; // This is the starting node - no response required.
				pendingRequest = new PendingRequest(startNode, key, value, expectResponse);
				pendingRequest.addNodesToNecessaryResponses(allNeighbours);
				localStorage.setPendingRequest(key, pendingRequest);
				localStorage.lock(key);
			} else {
				// no neighbours or only async
				// -> commit immediately
				localStorage.put(key, value);
			}
			for(PathLink syncPath : syncPaths) {
				InetSocketAddress address = NetworkConfiguration.createAddressFromString(syncPath.getTarget());
				boolean expectResponse = true;
				msgSender.sendWriteSuggestion(address.getHostName(), address.getPort(), key, value, startNode, expectResponse);
			}
			for(PathLink asyncPath : asyncPaths) {
				InetSocketAddress address = NetworkConfiguration.createAddressFromString(asyncPath.getTarget());
				boolean expectResponse = false;
				msgSender.sendWriteSuggestion(address.getHostName(), address.getPort(), key, value, startNode, expectResponse);
			}

			if(!syncPaths.isEmpty()) {
				assert(pendingRequest != null);
				if(!pendingRequest.isFinished()) {
					synchronized(pendingRequest) {
					    try {
					    	pendingRequest.wait();
					    } catch (InterruptedException e) {
					        // Happens if someone interrupts the thread.
					    }
					}
				}
				return pendingRequest.getResponse();
			} else {
				return new PutResponse(true);
			}
		}
	}
	
	public String get(String key) {
		String value = localStorage.get(key);
		if(value == null) {
			return "UNDEFINED";
		} else {
			return value;
		}
	}
	
	public void delete(String key) {
		
	}
	
}
