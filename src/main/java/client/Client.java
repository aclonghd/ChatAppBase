package client;


import javafx.scene.web.WebEngine;
import netscape.javascript.JSObject;


public class Client {
	private WebEngine webEngine;
	private ThreadRecord threadRecord;
	private ClientSpeaker clientSpeaker;
	private ClientSocketHandler clientSocketHandler;
	
	public Client(WebEngine webEngine) {
		this.webEngine = webEngine;
		
	}
	public void connectToServer() {
		try {
			System.out.println("TEST");
			clientSocketHandler = new ClientSocketHandler("127.0.0.1", 8080, webEngine);
			JSObject jsobj = (JSObject) webEngine.executeScript("window");
	        jsobj.setMember("clientSocket", clientSocketHandler);
			clientSocketHandler.start();
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		
		
	}
	public void startRecording() {
		threadRecord = new ThreadRecord();
		threadRecord.start();
	}
	public void stopRecording() {
		threadRecord.stopRecord();
	}
	public void playRecord() {
		byte[] buff =threadRecord.getbyte();
		
		clientSpeaker = new ClientSpeaker(buff);
		clientSpeaker.play();
		
		System.out.println(buff.length);
	}
	public void pauseRecord() {
		clientSpeaker.pause();
	}
	public void resumeRecord() {
		clientSpeaker.resume();
	}
	public void stopPlayRecord() {
		clientSpeaker.stop();
	}
	
	public byte[] getBufferRecord() {
		return threadRecord.getbyte();
	}
	
	public int getAudioRecordedDuration() {
		return (int) clientSpeaker.getAudioDuration();
	}
	
	public boolean isConnect() {
		try {
			return clientSocketHandler.isAlive();
		}
		catch (Exception e) {
			return false;
		}
	}
}