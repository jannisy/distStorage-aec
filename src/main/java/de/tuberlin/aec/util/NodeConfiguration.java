package de.tuberlin.aec.util;


/**
 * This class encapsulates the configuration of one Disto Node.
 *
 */
public class NodeConfiguration {
	
	int port;
	int restPort;
	private String host;

	public NodeConfiguration(String host, int port, int restPort) {
		this.host = host;
		this.port = port;
		this.restPort = restPort;
	}
	public String getHostAndPort() {
		return this.getHost() + ":" + this.getPort();
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
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
