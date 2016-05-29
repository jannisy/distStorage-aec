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

        String pathsConfig = args[0];
        String serverlist = args[1];
        String host = args[2];
        int hermesPort = new Integer(args[3]);
        int restPort = new Integer(args[4]);
        
        System.out.println("Disto. pathsConfig=" + pathsConfig + ", serverlist=" + serverlist + ",host=" + host + ", port=" + hermesPort + ", restPort=" + restPort);
        
        // TODO 
    	NodeConfiguration nodeConfig = new NodeConfiguration(host, hermesPort, restPort);
    	PathConfiguration pathConfig = new PathConfiguration(pathsConfig);
    	NetworkConfiguration  netConfig = new NetworkConfiguration(serverlist);
    	
    	DistoNode node = new DistoNode(nodeConfig, pathConfig, netConfig);
    	node.start();
    	
    }

	private static String[] addStandardValuesToArguments(String[] args) {
		String[] newArgs = new String[5];
		for(int i = 0; i < newArgs.length; i++) {
			if(i < args.length && !args[i].equals("-") && !args[i].equals("default")) {
				newArgs[i] = args[i];
			} else {
				if(i == 0) {
					newArgs[i] = "paths.xml";
				} else if(i == 1) {
					newArgs[i] = "serverlist";
				} else if(i == 2) {
					newArgs[i] = "localhost";
				} else if(i == 3) {
					newArgs[i] = "5000";
				} else if(i == 4) {
					newArgs[i] = "8080";
				}
			}
		}
    	return newArgs;
	}
}
