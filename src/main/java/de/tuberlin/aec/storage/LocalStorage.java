package de.tuberlin.aec.storage;

/**
 * This class represents the local storage of one node
 * of the key value store.
 *
 */
interface LocalStorage {

	/**
	 * returns the value with the given key
	 * @param key the key
	 * @return the value with the given key
	 */
	public String get(String key);
	
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

}
