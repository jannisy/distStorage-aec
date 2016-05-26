package de.tuberlin.aec.storage;

import java.util.HashMap;
import java.util.Map;

import de.tuberlin.aec.PendingRequest;


/**
 * A simple local storage using a Java map.
 *
 */
public class MapStorage implements LocalStorage {

	Map<String, String> map;
	Map<String, Boolean> locked;
	Map<String, PendingRequest> pendingRequests;
	
	public MapStorage() {
		map = new HashMap<String, String>();
		locked = new HashMap<String, Boolean>();
		pendingRequests = new HashMap<String, PendingRequest>();
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
		if(locked.containsKey(key)) {
			return this.locked.get(key);
		} else {
			return false;
		}
		
	}
	@Override
	public PendingRequest getPendingRequest(String key) {
		return pendingRequests.get(key);
	}
	@Override
	public void removePendingRequest(String key) {
		pendingRequests.remove(key);
	}
	@Override
	public void setPendingRequest(String key, PendingRequest request) {
		pendingRequests.put(key, request);
		
	}

}
