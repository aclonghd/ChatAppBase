package server;

import java.io.IOException;

public class ServerMain {
	public static void main(String[] args) {
		try {
			Server server = new Server(8080);
			server.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

}
