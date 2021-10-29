package server;

import java.util.ArrayList;
import java.util.List;

public class SearchEngine extends Thread {
	
	private List<String> list;
	
	public SearchEngine() {
		list = new ArrayList<>();
	}
	
	public void run() {
		while(true) {
			if(list.size() > 1) {
				try {
					// Đại khái là có 2 request tìm kiếm thì ghép cặp 2 socket vs nhau rồi xóa request đi
					String idClientRequest = list.get(0);
					String idClientMatch = list.get(1);
					if(idClientMatch != null) {
						list.remove(idClientRequest);
						list.remove(idClientMatch);
						
						// Gửi thông báo tìm kiếm thành công
						ServerSocketHandler.getMapSocketHandler().get(idClientRequest).notifyFindSuccess(idClientMatch);
						ServerSocketHandler.getMapSocketHandler().get(idClientMatch).notifyFindSuccess(idClientRequest);
						
					}
				} catch(IndexOutOfBoundsException ex) {
					continue;
				}
			} else
				waiting();
			
		}
		
	}
	
	public synchronized void waiting() {
		try {
			wait();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public synchronized void notifing() {
		notify();
	}
	
	public void addRequest(String idClientRequest) {
		list.add(idClientRequest);
		notifing();
	}
	
	public void removeRequest(String idClientRequest) {
		list.remove(idClientRequest);
	}
	
	

}
