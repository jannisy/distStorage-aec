package de.tuberlin.aec.message;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.tub.ise.hermes.Request;

/**
 * A DistoMessage is a Request with added functionality.
 * It uses a HashMap to save arbitrary Strings to the request payload.
 *
 */
public class DistoMessage extends Request{


	public DistoMessage(String target, String sender) {
		super((List<Serializable>) new ArrayList<Serializable>(), target, sender);
	}
	protected DistoMessage(List<Serializable> items, String target, String sender) {
		super(items, target, sender);
	}


	protected HashMap<String, String> getRequestMap() {
		List<Serializable> items = this.getItems();
		HashMap<String, String> map;
		if(items.size() == 0 || !(items.get(0) instanceof HashMap<?,?>)) {
			map = new HashMap<String, String>();
			this.addItem(map);
		} else {
			map = (HashMap<String, String>) items.get(0);
		}
		return map;
	}

}
