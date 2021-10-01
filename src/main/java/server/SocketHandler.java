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
				Object input = in.readObject();
				if(input != null) {
					RequestObject request = (RequestObject) input;
					switch(request.getEvent()) {
						
						case CONNECT_TO_SERVER:{
							response(new ResponseObject(StatusCode.OK));
							break;
						}
						case SEARCH_USER:{
							action = Action.SEARCHING;
							while((clientIdMatch = getRoomEngine().searchUser(getUUID(), action)) == null) {
								
							}
							response(new ResponseObject(StatusCode.OK));
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
									sendMessage(msg, clientIdMatch);
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
				getRoomEngine().removeClient(getUUID());
				getMapSocketHandler().remove(getUUID());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
	}
	
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
	
	public void sendMessage(String msg, String clientId) throws IOException {
		
		getMapSocketHandler().get(clientId).response(new MessageResponse(StatusCode.OK, msg));
	}
	
}
