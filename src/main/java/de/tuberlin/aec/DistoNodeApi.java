package de.tuberlin.aec;

import java.net.InetSocketAddress;
import java.util.List;

import de.tuberlin.aec.communication.MessageSender;
import de.tuberlin.aec.storage.LocalStorage;
import de.tuberlin.aec.util.NetworkConfiguration;
import de.tuberlin.aec.util.NodeConfiguration;
import de.tuberlin.aec.util.PathConfiguration;

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

	public void put(String key, String value) {
		System.out.println("# API PUT REQUEST: [" + key + ", " + value + "]");

		if(localStorage.isLocked(key)) {
			System.out.println("Another Put Request is pending. Key is currently locked. Abort request");
		} else {
			List<String> neighbours = pathConfig.getNodeNeighbours(nodeConfig.getHostAndPort(), nodeConfig.getHostAndPort());
			localStorage.lock(key);
			String startNode = nodeConfig.getHostAndPort();
			for(String neighbour : neighbours) {
				InetSocketAddress address = NetworkConfiguration.createAddressFromString(neighbour);
				msgSender.sendSyncWriteSuggestion(address.getHostName(), address.getPort(), key, value, startNode);
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
