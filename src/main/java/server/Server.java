package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.HashMap;
import java.util.Map;

import enums.Action;

public class Server {
	private ServerSocket server;
	private int portServer;
	
	//2 bien global de cac class goi dc den no
	public static RoomEngine roomEngine;
	public static Map<String, SocketHandler> mapSocketHandler;
	
	public Server(int portServer) throws IOException {
		this.portServer = portServer;
		Server.roomEngine = new RoomEngine();
		mapSocketHandler = new HashMap<>();
	}
	public void start() {
		try {
			server = new ServerSocket(portServer);
			while(true) {
				//Tao 1 thread ms xu ly socket ms khi co ket noi
				//Moi 1 socket se co thread moi xu ly cac su kien
				SocketHandler socketHandler = new SocketHandler(server.accept());
				socketHandler.start();
				mapSocketHandler.put(socketHandler.getUUID(), socketHandler);
				
			}
		} catch(IOException ex) {
			System.err.println(ex.getStackTrace().toString());
		}
		
	}
}
