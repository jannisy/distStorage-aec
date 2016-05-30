package de.tuberlin.aec;

/**
 * This class represents a pending synchronous request.
 * It is finished if all responses of the request have
 * arrived.
 *
 */
public class PendingSyncRequest extends PendingRequest {

	public PendingSyncRequest(String startNode, String key, String value, boolean expectResponse) {
		super(startNode, key, value, expectResponse);
	}

}
