package client;

import java.io.ObjectOutputStream;
import java.util.Scanner;

import enums.Event;
import model.MessageRequest;

public class ChattingProcess extends Thread {
	private ObjectOutputStream out;
	private boolean running;
	
	public ChattingProcess(ObjectOutputStream out) {
		this.out = out;
		this.running = true;
	}
	
	@Override
	public void run() {
		Scanner sc = new Scanner(System.in);
		try{
			while(running) {
				
				String message = sc.nextLine();
				if(!message.equals("")) {
					out.writeObject(new MessageRequest(Event.SEND_MESSAGE, message));
					System.out.println("Ban: " + message);
				}
			}
		} catch(Exception ex) {
			sc.close();
		}
		
	}
	public void stopThread() {
		running = false;
	}
}
