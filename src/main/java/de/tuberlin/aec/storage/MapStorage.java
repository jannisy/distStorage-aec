package de.tuberlin.aec.storage;

import java.util.HashMap;
import java.util.Map;


/**
 * A simple local storage using a Java map.
 *
 */
public class MapStorage implements LocalStorage {

	Map<String, String> map;
	Map<String, Boolean> locked;
	
	public MapStorage() {
		map = new HashMap<String, String>();
		locked = new HashMap<String, Boolean>();
		
	}
	public String get(String key) {
		return map.get(key);
	}

	public void put(String key, String value) {
		map.put(key, value);
		
	}

	public void delete(String key) {
		map.remove(key);
		
	}
	public void lock(String key) {
		locked.put(key, true);
		
	}
	public void unlock(String key) {
		locked.remove(key);
		
	}
	public boolean isLocked(String key) {
		return this.locked.get(key);
		
	}

}
