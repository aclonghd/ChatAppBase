package client;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;

public class ClientSpeaker {
	private byte[] buff;
	private Clip clip;
	private long clipTime;
	private final AudioFormat format= ThreadRecord.getAudioFormat();
	
	public ClientSpeaker(byte[] buffer) {
		this.buff = buffer;
	}
	public void play() {
		InputStream targetStream = new ByteArrayInputStream(buff);
		AudioInputStream audioInputStream = new AudioInputStream(targetStream, format, buff.length);
		try {
			clip = AudioSystem.getClip();
			clip.open(audioInputStream);
			clip.start();
		} catch (LineUnavailableException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void pause() {
		clipTime= clip.getMicrosecondPosition();
		System.out.println(clipTime);
		clip.stop();
	}
	public void resume() {
		clip.setMicrosecondPosition(clipTime);
		System.out.println("resume: " +clipTime);
		clip.start();
	}
	
	public void stop() {
		clip.stop();
	}
	
	public float getAudioDuration() {
		return (buff.length * 8) / (8000.0F * 16 * 2);
	}
}
