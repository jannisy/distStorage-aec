package de.tuberlin.aec;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

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

	/* the dependencies */
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

	/**
	 * performs a put request with the given key and value
	 * @param key the key
	 * @param value the value
	 * @return the response
	 */
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
			List<PathLink> quorum = pathConfig.getQuorumPathLinks(startNode, startNode);

			PendingRequest pendingRequest = null;

			if (!quorum.isEmpty()) {
				// start a quorum
				pendingRequest = new PendingQuorum(startNode, key, value, false, pathConfig.getQuorumSize(startNode));
				pendingRequest.addNodesToNecessaryResponses(allNeighbours);
				localStorage.setPendingRequest(key, pendingRequest);
				localStorage.lock(key);
			} else {
				if (!syncPaths.isEmpty()) {
					boolean expectResponse = false; // This is the starting node - no response required.
					if (localStorage.getPendingRequest(key) != null) {
						System.out.println("Internal Error: There is a pending request on this key.");
					}
					pendingRequest = new PendingRequest(startNode, key, value, expectResponse);
					pendingRequest.addNodesToNecessaryResponses(syncNeighbours);
					localStorage.setPendingRequest(key, pendingRequest);
					localStorage.lock(key);
				} else { // no neighbours or only async -> commit immediately
					localStorage.put(key, value);
				}
			}

			sendWriteSuggestions(syncPaths, key, value, startNode, true);
			sendWriteSuggestions(asyncPaths, key, value, startNode, false);
			sendWriteSuggestions(quorum, key, value, startNode, true);

			if (!syncPaths.isEmpty() || !syncPaths.isEmpty()) { //TODO involve quorum
				assert (pendingRequest != null);
				if (!pendingRequest.isFinished()) {
					synchronized (pendingRequest) {

						ScheduledThreadPoolExecutor exec = new ScheduledThreadPoolExecutor(1);
						exec.schedule(new TimeOutChecker(pendingRequest, key), REQUEST_TIMEOUT_MS, TimeUnit.MILLISECONDS);

						waitForResponses(pendingRequest);
					}
				}
				return pendingRequest.getResponse();
			} else {
				return new PutResponse(true);
			}

		}
	}

	private void waitForResponses(PendingRequest pendingRequest) {
		try {
			pendingRequest.wait();
			System.out.println("PendingRequest: notified");
		} catch (InterruptedException e) {
			e.printStackTrace(); // Happens if someone interrupts the thread.
		}
	}

	private void sendWriteSuggestions(List<PathLink> paths, String key, String value, String startNode, boolean expectResponse) {
		for (PathLink path : paths) {
			InetSocketAddress address = NetworkConfiguration.createAddressFromString(path.getTarget());
			msgSender.sendWriteSuggestion(address.getHostName(), address.getPort(), key, value, startNode, expectResponse);
		}
	}


	/**
	 * performs a get request with the given key and returns
	 * the value
	 * @param key the key
	 * @return the value
	 */
	public String get(String key) {
		String value = localStorage.get(key);
		if(value == null) {
			return "UNDEFINED";
		} else {
			return value;
		}
	}
	/**
	 * returns the time stamp for the given key
	 * the value
	 * @param key the key
	 * @return the time stamp
	 */
	public long getTimestamp(String key) {
		return localStorage.getTimestamp(key);
	}
	
	/**
	 * deletes the value with the given key
	 * @param key the key
	 */
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

	private class TimeOutChecker implements Runnable {

		private PendingRequest pendingRequest;
		private String key;

		public TimeOutChecker(PendingRequest pendingRequest, String key) {
			this.pendingRequest = pendingRequest;
			this.key = key;
		}

		public void run() {

			if (!pendingRequest.isFinished()) {
				System.out.println("Timeout for Put Request key=" + key);
				localStorage.unlock(key);
				localStorage.removePendingRequest(key);

				synchronized (pendingRequest) {
					pendingRequest.setFinished(true);
					PutResponse response = new PutResponse(false);
					response.setErrorMessage("Timeout!");
					pendingRequest.setResponse(response);
					System.out.println("Notify (timeout)");
					pendingRequest.notifyAll();
				}
			}
		}
	}
}
