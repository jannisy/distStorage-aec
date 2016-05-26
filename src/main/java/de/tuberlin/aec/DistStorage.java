package de.tuberlin.aec;

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
        
        String host = args[2];
        
        // TODO 
    	NodeConfiguration nodeConfig = new NodeConfiguration(host, hermesPort, serverPort);
    	PathConfiguration pathConfig = new PathConfiguration("config-sample.xml");
    	NetworkConfiguration  netConfig = new NetworkConfiguration("serverlist");
    	
    	DistoNode node = new DistoNode(nodeConfig, pathConfig, netConfig);
    	node.start();
    	
    }

	private static String[] addStandardValuesToArguments(String[] args) {
    	if(args.length == 0) {
    		args = new String[3];
    		args[0] = "5000";
    		args[1] = "8080";
			args[2] = "localhost";
    	} else if(args.length == 1) {
    		String temp = args[0];
    		args = new String[3];
    		args[0] = temp;
    		args[1] = "8080";
			args[2] = "localhost";
		} else if(args.length == 2) {
			String tempPort = args[0];
			String tempRestPort = args[1];
			args = new String[3];
			args[0] = tempPort;
			args[1] = tempRestPort;
			args[2] = "localhost";
		}
    	return args;
	}
}
