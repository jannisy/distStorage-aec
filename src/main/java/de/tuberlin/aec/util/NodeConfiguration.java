package de.tuberlin.aec.util;


/**
 * This class encapsulates the configuration of one Disto Node.
 *
 */
public class NodeConfiguration {
	
	int port;
	int restPort;

	public NodeConfiguration(int port, int restPort) {
		this.port = port;
		this.restPort = restPort;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public int getRestPort() {
		return restPort;
	}

	public void setRestPort(int restPort) {
		this.restPort = restPort;
	}
}
