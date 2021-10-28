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
	
	// UUID của client socket đang chat với socket này 
	private String clientIdMatch;
	private Action action;
	
	
	// Constructor cài đặt class
	public SocketHandler(Socket socket) throws IOException {
		this.socket = socket;
		id = UUID.randomUUID();
		out = new ObjectOutputStream(socket.getOutputStream());
		in = new ObjectInputStream(socket.getInputStream());
		
	}
	
	@Override
	public void run() {
		try {
			// Cờ flag dùng để thoát vòng lặp while
			boolean flag = true;
			while(flag) {
				// Đọc object gửi đến từ client
				Object input = in.readObject();
				if(input != null) {
					// Ép kiểu object sang request
					RequestObject request = (RequestObject) input;
					switch(request.getEvent()) {
						case CONNECT_TO_SERVER:{
							
							action = Action.WAITING;
							// Gửi response thành công về lại cho client
							response(new ResponseObject(StatusCode.REPLY_CONNECT_TO_SERVER));
							break;
						}
						case SEARCH_USER:{
							if(clientIdMatch != null && action == Action.CHATTING) {
								notifyDisconnect();
								clientIdMatch = null;
							}
							action = Action.SEARCHING;
							
							// Gửi response NO_CONTENT về client để thông báo request tìm người đã thành công
							response(new ResponseObject(StatusCode.REPLY_SEARCH_USER));
							
							// Thêm request tìm kiếm của socket này vào hàng đợi của searchEngine
							getSearchEngine().addRequest(getUUID());
							
							break;
						}
						case SEND_MESSAGE:{
							if(clientIdMatch == null) 
								response(new ResponseObject(StatusCode.BAD_REQUEST));
							else {
								String msg = ((MessageRequest) request).getMessage();
								if(msg != null)
									// Hàm gửi msg đến socket đang chat vs socket này
									sendMessage(msg);
							}
							
							break;
							
						}
						case DISCONNECT:{
							getMapSocketHandler().remove(getUUID());
							// Nếu socket này đang tìm kiếm thì xóa request tìm kiếm trong searchEngine
							if(action == Action.SEARCHING) {
								getSearchEngine().removeRequest(getUUID());
							}
							
							// Nếu đang chat thì thông báo đã thoát cho socket đang chat vs socket này
							if(clientIdMatch != null && action == Action.CHATTING) {
								notifyDisconnect();
							}
							response(new ResponseObject(StatusCode.NO_CONTENT));
							in.close();
							out.close();
							socket.close();
							
							// Thoát vòng lặp, thread này tự tắt
							flag = false;
							break;
						}
					default:
						break;
					}
					
				}
			}
			
		} catch(Exception ex) {
			try {
				// Đóng kết nối khi client mất kết nối, tắt ứng dụng,...
				out.close();
				in.close();
				socket.close();
				getMapSocketHandler().remove(getUUID());
				if(action == Action.SEARCHING) {
					getSearchEngine().removeRequest(getUUID());
				}
				
				if(clientIdMatch != null) notifyDisconnect();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
	}
	// Methods gui response ve cho client
	public void response(ResponseObject response) throws IOException {
		out.writeObject(response);
		out.flush();
	}
	
	public String getUUID() {
		return id.toString();
	}
	
	public SearchEngine getSearchEngine() {
		return ServerSocketHandler.getSearchEngine();
	}
	
	public Map<String, SocketHandler> getMapSocketHandler(){
		return ServerSocketHandler.getMapSocketHandler();
	}
	
	public void sendMessage(String msg) throws IOException {
		//Goi mapSocketHandler de lay socket cua nguoi dang chat roi gui tra MessageResponse
		getMapSocketHandler().get(clientIdMatch).response(new MessageResponse(StatusCode.OK, msg));
	}
	
	public void notifyDisconnect() {
		try {
			
			getMapSocketHandler().get(clientIdMatch).setClientIdMatch(null);
			getMapSocketHandler().get(clientIdMatch).response(new ResponseObject(StatusCode.USER_DISCONNECT));
		} catch(IOException ex) {
			Map<String, SocketHandler> mapSocketHandler = getMapSocketHandler();
			if(mapSocketHandler.get(clientIdMatch).action == Action.SEARCHING) {
				getSearchEngine().removeRequest(clientIdMatch);
			}
			mapSocketHandler.remove(clientIdMatch);
		}
		
		
	}
	
	public void setClientIdMatch(String clientIdMatch) {
		this.clientIdMatch = clientIdMatch;
	}
	
	public void notifyFindSuccess(String clientIdMatch) {
		action = Action.CHATTING;
		this.clientIdMatch = clientIdMatch;
		try {
			response(new ResponseObject(StatusCode.REPLY_FIND_SUCCESS));
		} catch (IOException e) {
			notifyDisconnect();
		}
	}
}
