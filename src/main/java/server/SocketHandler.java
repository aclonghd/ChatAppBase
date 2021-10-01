package server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Map;
import java.util.UUID;

import enums.Action;
import enums.StatusCode;
import model.RequestObject;
import model.ResponseObject;
import model.MessageRequest;
import model.MessageResponse;

public class SocketHandler extends Thread {
	private UUID id;
	private Socket socket;
	private ObjectOutputStream out;
	private ObjectInputStream in;
	private Action action;
	private String clientIdMatch;
	
	public SocketHandler(Socket socket) throws IOException {
		this.socket = socket;
		id = UUID.randomUUID();
		out = new ObjectOutputStream(socket.getOutputStream());
		in = new ObjectInputStream(socket.getInputStream());
	}
	
	@Override
	public void run() {
		try {
			
			while(true) {
				// Doc Object gui den tu server
				Object input = in.readObject();
				if(input != null) {
					//Ep kieu object sang request
					RequestObject request = (RequestObject) input;
					switch(request.getEvent()) {
						case CONNECT_TO_SERVER:{
							action = Action.WAITING;
							//Them client socket nay vao map cua RoomEngine	
							getRoomEngine().putClient(getUUID(), action);
							//Gui response thanh cong ve lai cho client
							response(new ResponseObject(StatusCode.OK));
							break;
						}
						case SEARCH_USER:{
							action = Action.SEARCHING;
							System.out.println("TEST");
							// Doi cho den khi tim dc mot client socket khac
							while((clientIdMatch = getRoomEngine().searchUser(getUUID(), action)) == null) {
								
							}
							//Gui response thanh cong ve lai cho client
							response(new ResponseObject(StatusCode.OK));
							
							//cai nay them vao de test :D
							out.writeUTF(clientIdMatch);
							out.flush();
							break;
						}
						case SEND_MESSAGE:{
							if(clientIdMatch == null) 
								response(new ResponseObject(StatusCode.BAD_REQUEST));
							else {
								String msg = ((MessageRequest) request).getMessage();
								if(msg != null)
									sendMessage(msg);
							}
							
							break;
							
						}
					default:
						break;
					}
					
				}
			}
			
		} catch(Exception ex) {
			try {
				out.close();
				in.close();
				socket.close();
				//Xoa khi client dong ket noi socket hoac thoat ung dung...
				getRoomEngine().removeClient(getUUID());
				getMapSocketHandler().remove(getUUID());
				//Thong bao den client dang chat vs client nay
				notifyDisconnect();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
	}
	//Methods gui response ve cho client
	public void response(ResponseObject response) throws IOException {
		out.writeObject(response);
		out.flush();
	}
	
	public String getUUID() {
		return id.toString();
	}
	
	public RoomEngine getRoomEngine() {
		return Server.roomEngine;
	}
	
	public Map<String, SocketHandler> getMapSocketHandler(){
		return Server.mapSocketHandler;
	}
	
	public void sendMessage(String msg) throws IOException {
		//Goi mapSocketHandler de lay socket cua nguoi dang chat roi gui tra MessageResponse
		getMapSocketHandler().get(clientIdMatch).response(new MessageResponse(StatusCode.OK, msg));
	}
	
	public void notifyDisconnect() throws IOException {
		getMapSocketHandler().get(clientIdMatch).response(new ResponseObject(StatusCode.USER_DISCONNECT));
	}
	
}
