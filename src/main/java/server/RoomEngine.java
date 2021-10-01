package server;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import enums.Action;

public class RoomEngine {
	private Map<String, Action> mapClient;

	
	public RoomEngine() {
		mapClient = new HashMap<String, Action>();
	}
	
	//Tim kiem nguoi la theo action cua client
	public String searchUser(String clientId1, Action action) {
		if(!mapClient.get(clientId1).equals(action))
			mapClient.replace(clientId1, action);
		for (Entry<String, Action> entry : mapClient.entrySet()) {
		    if(!entry.getKey().equals(clientId1) && entry.getValue() == Action.SEARCHING) {
		    	entry.setValue(Action.CHATTING);
		    	return entry.getKey();
		    }
		}
		return null;
	}
	
	//Them idClient va action vao trong map
	public void putClient(String idClient, Action action) {
		mapClient.put(idClient, action);
	}
	
	//Xoa
	public void removeClient(String idString) {
		mapClient.remove(idString);
	}
	
}
