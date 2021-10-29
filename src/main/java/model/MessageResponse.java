package model;


import enums.StatusCode;

@SuppressWarnings("serial")
public class MessageResponse extends ResponseObject{
	private String message;
	
	public MessageResponse(StatusCode statusCode, String message) {
		super(statusCode);
		this.message = message;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	
	
}
