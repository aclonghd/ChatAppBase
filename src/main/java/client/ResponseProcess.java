package client;

import java.io.ObjectInputStream;

import model.MessageResponse;

public class ResponseProcess extends Thread {
	private ObjectInputStream in;
	
	public ResponseProcess(ObjectInputStream in) {
		// TODO Auto-generated constructor stub
		this.in = in;
	}
	@Override
	public void run() {
		try {
			while(true) {
				Object object = in.readObject();
				if(object == null) {
					continue;
				} else {
					MessageResponse msgResponse = (MessageResponse) object;
					System.out.println("Nguoi la: " + msgResponse.getMessage());
				}
			}
		} catch(Exception ex) {
			
		}
	}
}
