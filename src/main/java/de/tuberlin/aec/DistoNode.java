package de.tuberlin.aec;

import java.io.IOException;

import de.tuberlin.aec.util.NetworkConfiguration;
import de.tuberlin.aec.util.NodeConfiguration;

/**
 * A DistoNode represents one node of the distributed key value store.
 * 
 * It reads the configurations and starts up the server for communication within
 * the storage system, as well as the web server for the REST API.
 * 
 */
public class DistoNode {

	/**
	 * the path configuration
	 */
	PathConfiguration pathConfig;
	/**
	 * the network configuration
	 */
	NetworkConfiguration networkConfig;
	/**
	 * the node configuration
	 */
	NodeConfiguration nodeConfig;
	/**
	 * creates a new DistoNode with the given configuration
	 * @param nodeConfig the node config
	 * @param pathConfig the path config
	 * @param networkConfig the network config
	 */
	public DistoNode(NodeConfiguration nodeConfig,
			PathConfiguration pathConfig, NetworkConfiguration networkConfig) {
		this.pathConfig = pathConfig;
		this.networkConfig = networkConfig;
		this.nodeConfig = nodeConfig;
	}
	
	/**
	 * starts this node
	 * @return
	 */
	public void start() {
		
		try {
			RestServer restServer = new RestServer(this.nodeConfig.getRestPort());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
