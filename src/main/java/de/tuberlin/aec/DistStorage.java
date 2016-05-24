package de.tuberlin.aec;

import com.sun.tools.doclets.formats.html.SourceToHTMLConverter;
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

        int port = new Integer(args[0]);
        System.out.println("Port set to " + port);

        String sender = "johannes";
        String target = "jannis";
        String targetIP = "192.168.0.57";

        Communicator communicator = new Communicator(port, sender);
        Request request = new Request("Cuntfuck", target, sender);
        communicator.sendMessage(targetIP, port, request);
    }
}
