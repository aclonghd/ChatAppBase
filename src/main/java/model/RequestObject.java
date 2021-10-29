package model;

import java.io.Serializable;

import enums.Event;

@SuppressWarnings("serial")
public class RequestObject implements Serializable {
	private Event event;

	public RequestObject(Event event) {
		this.event = event;
	}

	public Event getEvent() {
		return event;
	}

	public void setEvent(Event event) {
		this.event = event;
	}
	
	
}
