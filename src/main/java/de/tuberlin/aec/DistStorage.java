package de.tuberlin.aec;

import de.tub.ise.hermes.Request;
import de.tuberlin.aec.util.NodeConfiguration;
import de.tuberlin.aec.util.PathConfiguration;
import de.tuberlin.aec.util.NetworkConfiguration;

/**
 * Hello world!
 *
 */

public class DistStorage {
    public static void main(String[] args) {
    	
    	args = addStandardValuesToArguments(args);

        int hermesPort = new Integer(args[0]);
        System.out.println("Port set to " + hermesPort);

        int serverPort = new Integer(args[1]);
        System.out.println("Server Port set to " + serverPort);
        
    	NodeConfiguration nodeConfig = new NodeConfiguration(hermesPort, serverPort);
    	PathConfiguration pathConfig = new PathConfiguration("config-sample.xml");
    	NetworkConfiguration  netConfig = new NetworkConfiguration("serverlist");
    	
    	DistoNode node = new DistoNode(nodeConfig, pathConfig, netConfig);
    	node.start();
    	
    }

	private static String[] addStandardValuesToArguments(String[] args) {
    	if(args.length == 0) {
    		args = new String[2];
    		args[0] = "5000";
    		args[1] = "8080";
    	} else if(args.length == 1) {
    		String temp = args[0];
    		args = new String[2];
    		args[0] = temp;
    		args[1] = "8080";
    	}
    	return args;
	}
}
