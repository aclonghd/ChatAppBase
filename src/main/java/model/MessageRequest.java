package model;

import java.io.Serializable;

import enums.Event;

@SuppressWarnings("serial")
public class MessageRequest extends RequestObject {
	private String message;
	
	public MessageRequest(Event event, String message) {
		super(event);
		this.message = message;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	
	
	
}
