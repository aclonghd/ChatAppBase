package client;

import java.io.ObjectInputStream;
import javafx.application.Platform;

import enums.StatusCode;
import model.MessageResponse;
import model.ResponseObject;
import javafx.scene.web.WebEngine;

@SuppressWarnings("restriction")
public class ReceiveMsgProcess {
	private ObjectInputStream in;
	private boolean running;
	private WebEngine webEngine;
	
	public ReceiveMsgProcess(ObjectInputStream in, WebEngine webEngine) {
		// TODO Auto-generated constructor stub
		this.in = in;
		this.running = true;
		this.webEngine = webEngine;
	}
	
	public void startProcess() {
		try {
			//Tien trinh doi tin nhan den
			while(running) {
				Object object = null;
				if((object = in.readObject()) == null) {
					continue;
				} else if(object instanceof MessageResponse) {
					MessageResponse msgResponse = (MessageResponse) object;
					receiveMessage(msgResponse.getMessage());
					continue;
				} else if(object instanceof ResponseObject) {
					if(((ResponseObject) object).getStatusCode() == StatusCode.USER_DISCONNECT) {
						runLater("alertMsg('Người lạ đã thoát!');");
						stopProcess();
					} else if(((ResponseObject) object).getStatusCode() == StatusCode.NO_CONTENT){
						stopProcess();
					}
				}
			}
		} catch(Exception ex) {
			try {
				in.close();

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public void stopProcess() {
		running = false;
	}
	
	public void receiveMessage(String message) {
		String script = "recevieMsg('"+ message + "');";
		runLater(script);
	}
	
	public void runLater(String script) {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				webEngine.executeScript(script);
			}
		});
	}
	
}
