package client;
import javafx.scene.web.WebEngine;
import netscape.javascript.JSObject;


@SuppressWarnings("restriction")
public class Client {
	private WebEngine webEngine;
	
	public Client(WebEngine webEngine) {
		this.webEngine = webEngine;
	}
	public void connectToServer() {
		try {
			System.out.println("TEST");
			ClientSocketHandler clientSocketHandler = new ClientSocketHandler("127.0.0.1", 8080, webEngine);
			JSObject jsobj = (JSObject) webEngine.executeScript("window");
	        jsobj.setMember("clientSocket", clientSocketHandler);
			clientSocketHandler.start();
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		
		
	}
}
