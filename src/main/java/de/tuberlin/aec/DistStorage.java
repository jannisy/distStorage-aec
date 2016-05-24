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
        // TODO read ports from arguments or some file
    	NodeConfiguration nodeConfig = new NodeConfiguration(5000, 8080);
    	PathConfiguration pathConfig = new PathConfiguration("config-sample.xml");
    	NetworkConfiguration  netConfig = new NetworkConfiguration("serverlist");
    	
    	DistoNode node = new DistoNode(nodeConfig, pathConfig, netConfig);
    	node.start();

        int hermesPort = new Integer(args[0]);
        System.out.println("Port set to " + hermesPort);

        int serverPort = new Integer(args[1]);
        System.out.println("Port set to " + serverPort);

        String sender = "johannes";
        String target = "jannis";
        String targetIP = "192.168.0.57";

        Communicator communicator = new Communicator(hermesPort, sender);
        Request request = new Request("Cuntfuck", target, sender);
        communicator.sendMessage(targetIP, hermesPort, request);
    }
}
