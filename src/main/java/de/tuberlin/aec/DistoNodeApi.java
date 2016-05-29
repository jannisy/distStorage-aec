package de.tuberlin.aec;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import de.tub.ise.hermes.Sender;
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
	private NetworkConfiguration networkConfig;
	
	public static int REQUEST_TIMEOUT_MS = 15000;

	public DistoNodeApi(LocalStorage localStorage, MessageSender msgSender, 
			NodeConfiguration nodeConfig, PathConfiguration pathConfig, NetworkConfiguration networkConfig) {
		this.localStorage = localStorage;
		this.msgSender = msgSender;
		this.nodeConfig = nodeConfig;
		this.pathConfig = pathConfig;
		this.networkConfig = networkConfig;
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
			List<String> syncNeighbours = pathConfig.getSyncNeighbours(startNode, startNode);
			List<PathLink> syncPaths = pathConfig.getSyncNodePathLinks(startNode, startNode);
			List<PathLink> asyncPaths = pathConfig.getAsyncNodePathLinks(startNode, startNode);
			final PendingRequest pendingRequest;
			if(!syncPaths.isEmpty()) {
				boolean expectResponse = false; // This is the starting node - no response required.
				if(localStorage.getPendingRequest(key) != null) {
					System.out.println("Internal Error: There is a pending request on this key.");
				}
				pendingRequest = new PendingRequest(startNode, key, value, expectResponse);
				pendingRequest.addNodesToNecessaryResponses(syncNeighbours);
				localStorage.setPendingRequest(key, pendingRequest);
				localStorage.lock(key);
			} else {
				pendingRequest = null;
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

					    ScheduledThreadPoolExecutor exec = new ScheduledThreadPoolExecutor(1);
				
					    exec.schedule(new Runnable() {
					              public void run() {

					            	  if(!pendingRequest.isFinished()) {
						            	  System.out.println("Timeout for Put Request key=" + key);
						            	  localStorage.unlock(key);
						            	  localStorage.removePendingRequest(key);

						            	  synchronized(pendingRequest) {
						            		  pendingRequest.setFinished(true);
						            		  PutResponse response = new PutResponse(false);
						            		  response.setErrorMessage("Timeout!");
						            		  pendingRequest.setResponse(response);
						            		  System.out.println("Notify (timeout)");
						            		  pendingRequest.notifyAll();
						            	  }
					            	  }
					              }
					         }, REQUEST_TIMEOUT_MS, TimeUnit.MILLISECONDS);
					    
					    try {
					    	pendingRequest.wait();
					    	System.out.println("PendingRequest: notified");
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
	public long getTimestamp(String key) {
		return localStorage.getTimestamp(key);
	}
	
	public void delete(String key) {
		localStorage.unlock(key);
		localStorage.delete(key);
		for(InetSocketAddress node : networkConfig.getAllNodes()) {
			if(node.equals(nodeConfig.getSocket())) {
				// Don't send to this node
				continue;
			}
			msgSender.sendDeleteRequest(node.getHostName(), node.getPort(), key);
		}
	}
	
}
