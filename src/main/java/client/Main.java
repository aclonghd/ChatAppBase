package client;

public class Main {
	public static void main(String[] args) {
		Client client = new Client("127.0.0.1", 8080);
		try {
			client.connectToServer();
		} catch(Exception ex) {
			System.out.println("Mat ket noi den server");
		}
		
	}
}
