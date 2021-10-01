package client;

import java.io.ObjectInputStream;

import enums.StatusCode;
import model.MessageResponse;
import model.ResponseObject;

public class ReceiveMsgProcess extends Thread {
	private ObjectInputStream in;
	private boolean running;
	
	public ReceiveMsgProcess(ObjectInputStream in) {
		// TODO Auto-generated constructor stub
		this.in = in;
		this.running = true;
	}
	@Override
	public void run() {
		try {
			//Tien trinh doi tin nhan den
			while(running) {
				Object object = in.readObject();
				if(object == null) {
					continue;
				} else if(object instanceof MessageResponse) {
					MessageResponse msgResponse = (MessageResponse) object;
					System.out.println("Nguoi la: " + msgResponse.getMessage());
					continue;
				} else if(object instanceof ResponseObject) {
					if(((ResponseObject) object).getStatusCode() == StatusCode.USER_DISCONNECT) {
						System.out.println("Nguoi la da thoat");
						stopThread();
					}
				}
			}
		} catch(Exception ex) {
			
		}
	}
	
	public void stopThread() {
		running = false;
	}
}
