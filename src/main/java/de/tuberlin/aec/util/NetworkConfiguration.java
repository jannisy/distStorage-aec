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
	 * the standard port of a node
	 */
	public static final int STANDARD_PORT = 5000;
	
	/**
	 * the list of addresses of all nodes
	 */
	private List<InetSocketAddress> nodes;
	
	public NetworkConfiguration(String nodeListfile) {
		this.nodes = new ArrayList<InetSocketAddress>();
		this.readNodeList(nodeListfile);
	}
	
	public static InetSocketAddress createAddressFromString(String addressString) {

		InetSocketAddress address;
		if(addressString.contains(":")) {
			// host:port
			String[] addressParts = addressString.split(":");
			address = new InetSocketAddress(addressParts[0], Integer.parseInt(addressParts[1]));
		} else {
			// host
			address = new InetSocketAddress(addressString, NetworkConfiguration.STANDARD_PORT);
		}
		return address;
	}
	
	/**
	 * reads the node list file	
	 */
	private void readNodeList(String nodeListfile) {

		FileInputStream fstream;
		try {
			fstream = new FileInputStream(nodeListfile);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return;
		}
		BufferedReader br = new BufferedReader(new InputStreamReader(fstream));

		String strLine;
		try {
			while ((strLine = br.readLine()) != null)   {
				InetSocketAddress address = NetworkConfiguration.createAddressFromString(strLine);
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
