package de.tuberlin.aec.util;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class NetworkConfiguration {
	

	/**
	 * file which contains a list of all nodes
	 */
	public static final String NODE_LIST_FILE = "serverlist";
	
	/**
	 * the standard port of a node
	 */
	public static final int STANDARD_PORT = 5000;
	
	/**
	 * the list of addresses of all nodes
	 */
	private List<InetSocketAddress> nodes;
	
	public NetworkConfiguration() {
		this.nodes = new ArrayList<InetSocketAddress>();
		this.readNodeList();
	}
	
	/**
	 * reads the node list file	
	 */
	private void readNodeList() {

		FileInputStream fstream;
		try {
			fstream = new FileInputStream(NODE_LIST_FILE);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return;
		}
		BufferedReader br = new BufferedReader(new InputStreamReader(fstream));

		String strLine;
		try {
			while ((strLine = br.readLine()) != null)   {
				InetSocketAddress address;
				if(strLine.contains(":")) {
					// host:port
					String[] addressParts = strLine.split(":");
					address = new InetSocketAddress(addressParts[0], Integer.parseInt(addressParts[1]));
				} else {
					// host
					address = new InetSocketAddress(strLine, NetworkConfiguration.STANDARD_PORT);
				}
				this.nodes.add(address);
			}
			br.close();
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * returns the addresses of all nodes
	 * @return the addresses of all nodes
	 */
	public List<InetSocketAddress> getAllNodes() {
		return nodes;
	}
	
}
