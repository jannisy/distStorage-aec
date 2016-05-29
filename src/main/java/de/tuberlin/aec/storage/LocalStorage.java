package de.tuberlin.aec.storage;

import de.tuberlin.aec.PendingRequest;

/**
 * This class represents the local storage of one node
 * of the key value store.
 *
 */
public interface LocalStorage {

	/**
	 * returns the value with the given key
	 * @param key the key
	 * @return the value with the given key
	 */
	public String get(String key);

	/**
	 * returns the time stamp of the value with the given key
	 * @param key the key
	 * @returns the time stamp of the value with the given key
	 */
	public long getTimestamp(String key);
	
	/**
	 * puts the given key value pair
	 * @param key the key
	 * @param value the value
	 */
	public void put(String key, String value);
	
	/**
	 * deletes the value with the given key
	 * @param key the key
	 */
	public void delete(String key);

	/**
	 * locks the key value pair with the given key
	 * @param key the key
	 */
	public void lock(String key);

	/**
	 * unlocks the key value pair with the given key
	 * @param key the key
	 */
	public void unlock(String key);

	/**
	 * returns true if the key value pair with the given key is locked
	 * @param key the key
	 * @return true if the key value pair with the given key is locked
	 */
	public boolean isLocked(String key);

	/**
	 * returns the pending request for the given key
	 * @param key the key
	 * @return the pending request for the given key
	 */
	public PendingRequest getPendingRequest(String key);
	/**
	 * deletes the pending request for the given key
	 * @param key the key
	 */
	public void removePendingRequest(String key);
	/**
	 * sets the pending request for the given key.
	 * @param key the key
	 */
	public void setPendingRequest(String key, PendingRequest request);
	

}
