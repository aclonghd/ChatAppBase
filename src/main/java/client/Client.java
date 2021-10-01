package client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;

import enums.Event;
import enums.StatusCode;
import model.MessageRequest;
import model.RequestObject;
import model.ResponseObject;

public class Client {
	private Socket socket;
	private int numberPort;
	private String hostName;
	
	public Client(String hostname, int numberPort) {
		this.hostName = hostname;
		this.numberPort = numberPort;
		
	}
	public void connectToServer() throws IOException, ClassNotFoundException {
		this.socket = new Socket(hostName, numberPort);
		ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
		ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
		boolean flag = false;
		//Connect to server
		RequestObject request = new RequestObject(Event.CONNECT_TO_SERVER);
		out.writeObject(request);
		
		if(((ResponseObject) in.readObject()).getStatusCode() == StatusCode.OK) System.out.println("Ket noi thanh cong");
		
		//Search user to chat
		out.writeObject(new RequestObject(Event.SEARCH_USER));
		if(((ResponseObject) in.readObject()).getStatusCode() == StatusCode.OK) {
			flag = true;
			System.out.println("Tim thay nguoi la\nID nguoi la: " + in.readUTF() +"\nBat dau chat");
		}
		//Start chatting!
		ResponseProcess responseProcess = new ResponseProcess(in);
		responseProcess.start();
		while(flag) {
			Scanner sc = new Scanner(System.in);
			String message = sc.nextLine();
			if(!message.equals("")) {
				out.writeObject(new MessageRequest(Event.SEND_MESSAGE, message));
				System.out.println("Ban: " + message);
			}
			
		}
	}
}
