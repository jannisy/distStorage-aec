package de.tuberlin.aec;

import java.io.IOException;
import java.util.List;

import de.tub.ise.hermes.Receiver;
import de.tub.ise.hermes.RequestHandlerRegistry;
import de.tuberlin.aec.communication.DeleteRequestHandler;
import de.tuberlin.aec.communication.MessageSender;
import de.tuberlin.aec.communication.WriteCommitHandler;
import de.tuberlin.aec.communication.WriteSuggestionHandler;
import de.tuberlin.aec.communication.WriteSuggestionResponseHandler;
import de.tuberlin.aec.storage.MapStorage;
import de.tuberlin.aec.storage.LocalStorage;
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
	public static final String HANDLER_DELETE_REQUEST = "DeleteRequest";

	/**
	 * the path configuration
	 */
	private PathConfiguration pathConfig;
	/**
	 * the network configuration
	 */
	private NetworkConfiguration networkConfig;
	
	/**
	 * the node configuration
	 */
	private NodeConfiguration nodeConfig;
	
	/**
	 * the disto node api
	 */
	private DistoNodeApi api;
	/** 
	 * the local storage 
	 */
	private LocalStorage localStorage;
	private MessageSender msgSender;
	
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
		
		msgSender = new MessageSender(nodeConfig.getHostAndPort());
		localStorage = new MapStorage();
		api = new DistoNodeApi(localStorage, msgSender, nodeConfig, pathConfig, networkConfig);
		
	}
	
	/**
	 * starts this node
	 * @return
	 */
	public void start() {
		startWebserver();
		startHermes();
		
	}
	
	private void startWebserver() {

		try {
			RestServer restServer = new RestServer(this.nodeConfig.getRestPort(), this.api);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	private void startHermes() {

		/* Handlers */
		WriteSuggestionHandler syncWriteSuggestionHandler = new WriteSuggestionHandler(localStorage, pathConfig, nodeConfig, msgSender);
		WriteSuggestionResponseHandler syncWriteSuggestionResponseHandler = new WriteSuggestionResponseHandler(localStorage, pathConfig, nodeConfig, networkConfig, msgSender);
		WriteCommitHandler syncWriteCommitHandler = new WriteCommitHandler(localStorage, pathConfig, nodeConfig, msgSender);
		DeleteRequestHandler deleteRequestHandler = new DeleteRequestHandler(localStorage);
		
		RequestHandlerRegistry reg = RequestHandlerRegistry.getInstance();
		reg.registerHandler(DistoNode.HANDLER_SYNC_WRITE_COMMIT, syncWriteCommitHandler);
		reg.registerHandler(DistoNode.HANDLER_SYNC_WRITE_SUGGESTION, syncWriteSuggestionHandler);
		reg.registerHandler(DistoNode.HANDLER_SYNC_WRITE_SUGGESTION_RESPONSE, syncWriteSuggestionResponseHandler);
		reg.registerHandler(DistoNode.HANDLER_DELETE_REQUEST, deleteRequestHandler);
		
		try {
			Receiver receiver = new Receiver(nodeConfig.getPort());
			receiver.start();
			System.out.println("Receiver started.");
		} catch (IOException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
	}

}
