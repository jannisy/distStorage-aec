package de.tuberlin.aec.storage;

import java.util.HashMap;
import java.util.Map;

import de.tuberlin.aec.PendingRequest;


/**
 * A simple local storage using a Java map.
 *
 */
public class MapStorage implements LocalStorage {

	Map<String, Value> map;
	Map<String, Boolean> locked;
	Map<String, PendingRequest> pendingRequests;
	
	public MapStorage() {
		map = new HashMap<String, Value>();
		locked = new HashMap<String, Boolean>();
		pendingRequests = new HashMap<String, PendingRequest>();
	}
	public String get(String key) {
		Value val = map.get(key);
		if(val != null) {
			return val.getValue();
		} else {
			return null;
		}
	}
	public long getTimestamp(String key) {
		Value val = map.get(key);
		if(val != null) {
			return val.getTimestamp();
		} else {
			return -1;
		}
	}

	public void put(String key, String value) {
		Value v = new Value(value, System.currentTimeMillis());
		map.put(key, v);
		
	}

	public void delete(String key) {
		map.remove(key);
		
	}
	public void lock(String key) {
		System.out.println("LOCK " + key);
		locked.put(key, true);
		
	}
	public void unlock(String key) {
		System.out.println("UNLOCK " + key);
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
