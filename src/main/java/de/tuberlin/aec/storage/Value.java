package de.tuberlin.aec.storage;

/**
 * A storage value which constist of a string and a timestamp
 *
 */
class Value {
	private String value;
	private long timestamp;
	public Value(String value, long timestamp) {
		this.value = value;
		this.timestamp = timestamp;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public long getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}
}