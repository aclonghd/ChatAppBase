package client;

import java.io.IOException;
import java.io.ObjectInputStream;
import javafx.application.Platform;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import enums.Event;
import model.FileRequest;
import model.FileResponse;
import model.MessageRequest;
import model.MessageResponse;
import model.RequestObject;
import model.ResponseObject;
import netscape.javascript.JSObject;
import javafx.scene.web.WebEngine;

public class ClientSocketHandler extends Thread {
	private Socket socket;
	private int numberPort;
	private String hostName;
	private boolean isConnected;
	private WebEngine webEngine;
	private ObjectOutputStream out;
	private ObjectInputStream in;
	private boolean isChatting;
	private List<FileResponse> attachmentList;
	private Map<String, ClientSpeaker> mapSpeaker;
	private byte[] cacheDataBytesImg;
	public ClientSocketHandler(String hostname, int numberPort, WebEngine webEngine) {
		this.hostName = hostname;
		this.numberPort = numberPort;
		this.webEngine = webEngine;
		this.attachmentList = new ArrayList<FileResponse>();
		this.mapSpeaker = new HashMap<>();
	}
	@Override
	public void run() {
		System.out.println(hostName + " " + numberPort);
		isConnected = true;
		isChatting = false;
		try {
			socket = new Socket(hostName, numberPort);
			out = new ObjectOutputStream(socket.getOutputStream());
			in = new ObjectInputStream(socket.getInputStream());
			RequestObject request = new RequestObject(Event.CONNECT_TO_SERVER);
			sendRequest(request);
			while(isConnected) {
				Object input = in.readObject();
				if(input != null) {
					if(input instanceof MessageResponse && isChatting) {
						MessageResponse msgResponse = (MessageResponse) input;
						displayMessage(msgResponse.getMessage());
					} else if(input instanceof FileResponse && isChatting) {
						FileResponse file = (FileResponse) input;
						
						if(file.getFileType().equals("audio")) {
							ClientSpeaker newSpeaker = new ClientSpeaker(file.getDataBytes());
							mapSpeaker.put(file.getFilename(), newSpeaker);
							displayAudioMsg(file.getFilename(), (int) mapSpeaker.get(file.getFilename()).getAudioDuration());
						} else if(file.getFileType().split("/")[0].equals("image")){
							System.out.println(file.getFilename());
							cacheDataBytesImg = file.getDataBytes();
							displayImageMsg(file.getFilename());
						}
						else {
							attachmentList.add(file);
							displayAttachment(file.getFilename(), file.getFileSize());
						}
					} else if(input instanceof ResponseObject) {
					// Ép kiểu object sang request
						ResponseObject response = (ResponseObject) input;
						switch(response.getStatusCode()) {
							case REPLY_CONNECT_TO_SERVER:{
								System.out.println("Ket noi thanh cong");
								runLater("connectSuccess();");
								break;
							}
							case REPLY_SEARCH_USER:{
								runLater("displayLoading('Đang tìm kiếm người lạ');");
								break;
							}
							case REPLY_FIND_SUCCESS:{
								runLater("alertMsg('Đã tìm thấy người lạ! Bắt đầu nói chuyện.');");
								isChatting = true;
								break;
							} 
							case USER_DISCONNECT:{
								isChatting = false;
								runLater("alertMsg('Người lạ đã thoát!');");
								break;
							}
							case REPLY_DISCONNECT:{
								isConnected = false;
								break;
							}
							default:
								break;
						}
					}
				}	
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			isConnected = false;
			runLater("error();");
			return;
		}
		
	}
	public void sendFile(Object object) {
		JSObject file = (JSObject) object;
		String filename = (String) file.getMember("filename");
		String fileType = (String) file.getMember("fileType");
		int fileSize = (int) file.getMember("fileSize");
		String stringBuffer = (String) file.getMember("dataBytes");
		String[] arrayStrBuffer = stringBuffer.split(",");
		byte[] buffer = new byte[fileSize];
		System.out.println(filename);
		for(int i = 0; i < fileSize; i++) {
			buffer[i] = (byte) Integer.parseInt(arrayStrBuffer[i]);
		}
		FileRequest fileRq = new FileRequest(filename, fileSize, buffer, fileType, Event.SEND_FILE);
		try {
			sendRequest(fileRq);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		FileResponse fileRp = new FileResponse(filename, fileSize, buffer, fileType, null);
		attachmentList.add(fileRp);
		
	}
	public void downloadFile(String filename) {
		System.out.println("ooke");
		for(FileResponse file: attachmentList) {
			if(file.getFilename().equals(filename)) {
				FileChooserSavingFile fileChooser = new FileChooserSavingFile(file);
				fileChooser.start(null);
				return;
			}
		}
	}
	
	public void sendRequest(RequestObject request) throws IOException {
		out.writeObject(request);
	}
	
	public void search() {
		try {
			sendRequest(new RequestObject(Event.SEARCH_USER));
		} catch(Exception ex) {
			
		}
		
	}
	public void sendMessage(String message) throws IOException {
		if(!message.equals("")) {
			System.out.println("message: " + message);
			out.writeObject(new MessageRequest(Event.SEND_MESSAGE, message));
			System.out.println("Ban: " + message);
		}
	}
	
	public void sendAudio(String filename, byte[] buff) {
		FileRequest fileRq = new FileRequest(filename, buff.length, buff, "audio", Event.SEND_FILE);
		try {
			sendRequest(fileRq);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ClientSpeaker newSpeaker = new ClientSpeaker(buff);
		mapSpeaker.put(filename, newSpeaker);
	}
	
	public void displayMessage(String message) {
		String script = String.format("displayMsg(`%s`);", message);
		runLater(script);
	}
	
	public void displayAttachment(String filename, int fileSize) {
		
		String script = String.format("displayAttaMsg(`%s`, `%s`);", filename, fileSize);
		runLater(script);
		System.out.println(filename);
	}
	public void displayAudioMsg(String filename, int fileSize) {
		String script = String.format("displayAudioMsg(`%s`, `%d`);", filename, fileSize);
		runLater(script);
	}
	
	public void displayImageMsg(String filename) {
		String script = String.format("displayImageMsg(`%s`);", filename);
		runLater(script);
		
	}
	
	public void playAudioWithUUID(String uuid) {
		if(mapSpeaker.containsKey(uuid)) {
			mapSpeaker.get(uuid).play();
		}
	}
	
	public void pauseAudioWithUUID(String uuid) {
		if(mapSpeaker.containsKey(uuid)) {
			mapSpeaker.get(uuid).pause();
		}
	}
	
	public void resumeAudioWithUUID(String uuid) {
		if(mapSpeaker.containsKey(uuid)) {
			mapSpeaker.get(uuid).resume();
		}
	}
	
	public void stopAudioWithUUID(String uuid) {
		if(mapSpeaker.containsKey(uuid)) {
			mapSpeaker.get(uuid).stop();
		}
	}
	
	public int getAudioDurationWithUUID(String uuid) {
		if(mapSpeaker.containsKey(uuid)) {
			return (int) mapSpeaker.get(uuid).getAudioDuration();
		}
		return 0;
	}
	public byte[] getCacheDataImg() {
		byte[] res = cacheDataBytesImg;
		cacheDataBytesImg = null;
		return res;
	}
	
	public void connectToServer() throws IOException {
		RequestObject request = new RequestObject(Event.CONNECT_TO_SERVER);
		sendRequest(request);
	}
	
	public void runLater(String script) {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				webEngine.executeScript(script);
			}
		});
	}
	
	public void closeSocket() {
		System.out.println("DA Dong Cong");
		attachmentList.clear();
		isConnected = false;
		try {
			out.writeObject(new RequestObject(Event.DISCONNECT));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
