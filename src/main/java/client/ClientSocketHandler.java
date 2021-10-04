package client;

import java.io.IOException;
import java.io.ObjectInputStream;
import javafx.application.Platform;
import java.io.ObjectOutputStream;
import java.net.Socket;

import enums.Event;
import enums.StatusCode;
import model.MessageRequest;
import model.RequestObject;
import model.ResponseObject;
import netscape.javascript.JSObject;
import javafx.scene.web.WebEngine;

@SuppressWarnings("restriction")
public class ClientSocketHandler extends Thread {
	private Socket socket;
	private int numberPort;
	private String hostName;
	private boolean isConnected;
	private WebEngine webEngine;
	private ReceiveMsgProcess responseProcess;
	private ObjectOutputStream out;
	private ObjectInputStream in;
	
	public ClientSocketHandler(String hostname, int numberPort, WebEngine webEngine) {
		this.hostName = hostname;
		this.numberPort = numberPort;
		this.webEngine = webEngine;
	}
	@Override
	public void run() {
		System.out.println(hostName + " " + numberPort);
		isConnected = false;
		try {
			socket = new Socket(hostName, numberPort);
			out = new ObjectOutputStream(socket.getOutputStream());
			in = new ObjectInputStream(socket.getInputStream());
			
			//Ket noi den Server
			RequestObject request = new RequestObject(Event.CONNECT_TO_SERVER);
			out.writeObject(request);
			
			//Nhan response tu server
			if(((ResponseObject) in.readObject()).getStatusCode() == StatusCode.OK) {
				isConnected = true;
				System.out.println("Ket noi thanh cong");
				runLater("connectSuccess();");
				
			}
			while(isConnected) {
				out.writeObject(new RequestObject(Event.SEARCH_USER));
				if(((ResponseObject) in.readObject()).getStatusCode() == StatusCode.NO_CONTENT) runLater("alertMsg('Đang tìm kiếm người lạ');");
				if(((ResponseObject) in.readObject()).getStatusCode() == StatusCode.OK) {
					runLater("alertMsg('Đã tìm thấy người lạ! Bắt đầu nói chuyện nào.');");

					ChattingProcess chat = new ChattingProcess(out);
					Platform.runLater(new Runnable() {
						@Override
						public void run() {
							JSObject jsobj = (JSObject) webEngine.executeScript("window");
							
							jsobj.setMember("chattingProcess", chat);
							System.out.println("Inject Success");
						}
					});
					responseProcess = new ReceiveMsgProcess(in, webEngine);
					responseProcess.startProcess();
					
					
				} else {
					isConnected = false;
				}
			
				
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			isConnected = false;
			runLater("error();");
			return;
		}
		
	}
	public void runLater(String script) {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				webEngine.executeScript(script);
			}
		});
	}
	
	public void closeSocket() {
		System.out.println("DA Dong Cong");
		isConnected = false;
		try {
			out.writeObject(new RequestObject(Event.DISCONNECT));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public class ChattingProcess {
		private ObjectOutputStream out;
		
		public ChattingProcess(ObjectOutputStream out) {
			this.out = out;
		}
		
		public void sendMessage(String message) throws IOException {
			if(!message.equals("")) {
				System.out.println("message: " + message);
				out.writeObject(new MessageRequest(Event.SEND_MESSAGE, message));
				System.out.println("Ban: " + message);
			}
		}
	}
}
