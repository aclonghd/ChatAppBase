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
		//Ket noi den Server
		RequestObject request = new RequestObject(Event.CONNECT_TO_SERVER);
		out.writeObject(request);
		
		//Nhan response tu server
		if(((ResponseObject) in.readObject()).getStatusCode() == StatusCode.OK) {
			System.out.println("Ket noi thanh cong");
			flag = true;
		}
		while(flag) {
			System.out.println("Dang tim kiem nguoi la");
			
			//Gui request co event search_user den server
			out.writeObject(new RequestObject(Event.SEARCH_USER));
			
			//Nhan response tu server
			if(((ResponseObject) in.readObject()).getStatusCode() == StatusCode.OK) 
				System.out.println("Tim thay nguoi la\nID nguoi la: " + in.readUTF() +"\nBat dau chat");
		
			//Tao 1 luong xu ly response tu server
			ReceiveMsgProcess responseProcess = new ReceiveMsgProcess(in);
			responseProcess.start();
			
			//Tao 1 luong xu ly gui msg den server
			ChattingProcess sendMessageRequestProcess = new ChattingProcess(out);
			sendMessageRequestProcess.start();
			
			//Vong lap doi cho den khi nguoi la thoat
			while(responseProcess.isAlive()) {
				
			}
			//Kill thread
			sendMessageRequestProcess.stopThread();
		}
		
	}
}
