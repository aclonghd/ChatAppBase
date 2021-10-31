package client;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;

public class ThreadRecord extends Thread {
	private TargetDataLine dataLine;
	private byte[] data;
	private boolean flag;
	private int length;
	
	@Override
	public void run() {
		
		AudioFormat format = getAudioFormat();
        DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
        try {
			dataLine = (TargetDataLine) AudioSystem.getLine(info);
			dataLine.open(format);
			byte[] buff = new byte[1024];
			byte[] cache = new byte[1000000];
	        dataLine.start();
	        System.out.println("Recording");
	        flag = true;
	        length = 0;
			while(flag) {
				dataLine.read(buff, 0, buff.length);
				for(int i = 0; i < 1024; i++) {
					cache[length * 1024 + i] = buff[i];
				}
				length++;
				
			}
			
			data = new byte[(length - 1) * 1024];
			for(int i = 0; i < data.length; i++)
				data[i] = cache[i];
		} catch (LineUnavailableException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
	}
	
	public void stopRecord() {
		
		flag = false;
		dataLine.stop();
		dataLine.close();
		stop();
	}
	public byte[] getbyte() {
		
		return data;
	}
	
	public static AudioFormat getAudioFormat(){
		
        AudioFormat format = new AudioFormat(8000.0F, 16, 2, true, false);
        return format;
    }
}
