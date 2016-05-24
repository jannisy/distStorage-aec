package de.tuberlin.aec;

import java.io.IOException;

import de.tub.ise.hermes.Receiver;
import de.tub.ise.hermes.RequestHandlerRegistry;
import de.tuberlin.aec.communication.SyncWriteCommitHandler;
import de.tuberlin.aec.communication.SyncWriteSuggestionHandler;
import de.tuberlin.aec.communication.SyncWriteSuggestionResponseHandler;
import de.tuberlin.aec.util.NetworkConfiguration;
import de.tuberlin.aec.util.NodeConfiguration;
import de.tuberlin.aec.util.PathConfiguration;

/**
 * A DistoNode represents one node of the distributed key value store.
 * 
 * It reads the configurations and starts up the server for communication within
 * the storage system, as well as the web server for the REST API.
 * 
 */
public class DistoNode {
	

	public static final String HANDLER_SYNC_WRITE_COMMIT = "SyncWriteCommit";
	public static final String HANDLER_SYNC_WRITE_SUGGESTION = "SyncWriteSuggestion";
	public static final String HANDLER_SYNC_WRITE_SUGGESTION_RESPONSE = "SyncWriteSuggestionResp";

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
		
		/* Handlers */
		SyncWriteSuggestionHandler syncWriteSuggestionHandler = new SyncWriteSuggestionHandler();
		SyncWriteSuggestionResponseHandler syncWriteSuggestionResponseHandler = new SyncWriteSuggestionResponseHandler();
		SyncWriteCommitHandler syncWriteCommitHandler = new SyncWriteCommitHandler();
		
		RequestHandlerRegistry reg = RequestHandlerRegistry.getInstance();
		reg.registerHandler(DistoNode.HANDLER_SYNC_WRITE_COMMIT, syncWriteCommitHandler);
		reg.registerHandler(DistoNode.HANDLER_SYNC_WRITE_SUGGESTION, syncWriteSuggestionHandler);
		reg.registerHandler(DistoNode.HANDLER_SYNC_WRITE_SUGGESTION_RESPONSE, syncWriteSuggestionResponseHandler);
		
		try {
			Receiver receiver = new Receiver(nodeConfig.getPort());
			receiver.start();
			System.out.println("Receiver started.");
		} catch (IOException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
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
