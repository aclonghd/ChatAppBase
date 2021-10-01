package model;

import java.io.Serializable;

import enums.StatusCode;

@SuppressWarnings("serial")
public class ResponseObject implements Serializable {
	private StatusCode statusCode;

	public ResponseObject(StatusCode statusCode) {
		this.statusCode = statusCode;
	}

	public StatusCode getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(StatusCode statusCode) {
		this.statusCode = statusCode;
	}
	
}
