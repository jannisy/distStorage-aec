package de.tuberlin.aec;

import de.tuberlin.aec.util.NetworkConfiguration;
import de.tuberlin.aec.util.NodeConfiguration;

/**
 * Hello world!
 *
 */
public class DistStorage {
    public static void main( String[] args ) {
    	// TODO read ports from arguments or some file
    	NodeConfiguration nodeConfig = new NodeConfiguration(5000, 8080);
    	PathConfiguration pathConfig = new PathConfiguration("config-sample.xml");
    	NetworkConfiguration  netConfig = new NetworkConfiguration("serverlist");
    	
    	DistoNode node = new DistoNode(nodeConfig, pathConfig, netConfig);
    	node.start();
    	
    }
}
