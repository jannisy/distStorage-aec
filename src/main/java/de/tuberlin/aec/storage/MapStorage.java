package de.tuberlin.aec.storage;

import java.util.HashMap;
import java.util.Map;


/**
 * A simple local storage using a Java map.
 *
 */
public class MapStorage implements LocalStorage {

	Map<String, String> map;
	
	public MapStorage() {
		map = new HashMap<String, String>();
		
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

}
