package de.tuberlin.aec.util;

/**
 * This class represents a link in a path of the node architecture.
 * It is used like in the configuration file.
 *
 */
public class PathLink {

	private String type;

	private String source;
	private String target;

	public PathLink(String type, String source, String target) {
		this.type = type;
		this.source = source;
		this.target = target;
	}


	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getTarget() {
		return target;
	}

	public void setTarget(String target) {
		this.target = target;
	}
	
}
