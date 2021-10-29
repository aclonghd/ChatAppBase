package client;

import javafx.scene.web.WebEngine;
import netscape.javascript.JSObject;


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
//	public void copyFile(Object obj) {
//		JSObject file = (JSObject) obj;
//		String filename = (String) file.getMember("filename");
//		String fileType = (String) file.getMember("fileType");
//		int fileSize = (int) file.getMember("fileSize");
//		String stringBuffer = (String) file.getMember("dataBytes");
//		String[] arrayStrBuffer = stringBuffer.split(",");
//		byte[] buffer = new byte[fileSize];
//		for(int i = 0; i < fileSize; i++) {
//			buffer[i] = (byte) Integer.parseInt(arrayStrBuffer[i]);
//			
//		}
//		FileResponse fileRq = new FileResponse(filename, fileSize, buffer, fileType, StatusCode.OK);
//		FileChooserSavingFile fileChooser = new FileChooserSavingFile(fileRq);
//		fileChooser.start(null);
//		try {
//			fileChooser.stop();
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		try {
//			
//			JSObject file = (JSObject) obj;
//			dir += (String) file.getMember("filename");
//			System.out.println(dir);
//			FileOutputStream os = new FileOutputStream(dir);
//			String string = (String) file.getMember("dataBytes");
//			String[] array = string.split(",");
//			byte[] buffer = new byte[array.length];
//			for(int i = 0; i < array.length; i++) {
//				buffer[i] = (byte) Integer.parseInt(array[i]);
//			}
//	        for(int i = 0; i < buffer.length; i++) {
//	        	os.write(buffer[i]);
////	        	System.out.println(string);
//	        }
//	        os.close();
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
        
		
//	}
}