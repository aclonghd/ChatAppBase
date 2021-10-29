package server;

import java.io.IOException;

public class ServerMain {
	public static void main(String[] args) {
		// Main
		try {
			ServerSocketHandler server = new ServerSocketHandler(8080);
			server.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	

}
