package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.HashMap;
import java.util.Map;


public class ServerSocketHandler {
	private ServerSocket server;
	private int portServer;
	
	// Hai biến tĩnh của class
	private static SearchEngine searchEngine;
	// MapSocketHandler để lưu các SocketHandler đang kết nối
	private static Map<String, SocketHandler> mapSocketHandler;
	
	
	// Constructor cài đặt class
	public ServerSocketHandler(int portServer) throws IOException {
		this.portServer = portServer;
		searchEngine = new SearchEngine();
		mapSocketHandler = new HashMap<>();
	}
	
	public void start() {
		try {
			server = new ServerSocket(portServer);
			searchEngine.start();
			while(true) {
				// SocketHandler kế thừa thread, để xử lý các sự kiện của socket khi server.accept
				SocketHandler socketHandler = new SocketHandler(server.accept());
				
				// Chạy thread
				socketHandler.start();
				
				// Lưu socketHandler vào map
				mapSocketHandler.put(socketHandler.getUUID(), socketHandler);
				
			}
		} catch(IOException ex) {
			System.err.println(ex.getStackTrace().toString());
		}
		
	}
	
	public static SearchEngine getSearchEngine() {
		return searchEngine;
	}
	
	public static Map<String, SocketHandler> getMapSocketHandler() {
		return mapSocketHandler;
	}
}
