package de.tuberlin.aec.communication;

/**
 * This class represents the response for a put request.
 */
public class PutResponse {

	private boolean success;
	private String errorMessage;
	
	public PutResponse(boolean success) {
		this.success = success;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public boolean isSuccess() {
		return success;
	}
}
