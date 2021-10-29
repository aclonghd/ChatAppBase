/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server_voice;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.sampled.SourceDataLine;

/**
 *
 * @author admin
 */
public class player_thread extends Thread {
    public DatagramSocket din;
    public SourceDataLine audio_out;
    byte[] buffer = new byte[512];
    @Override
    public void run(){
        int i=0;
        DatagramPacket incoming = new DatagramPacket(buffer,buffer.length);
        while(server_voice.calling){
            try {
                din.receive(incoming);
                buffer = incoming.getData();
                audio_out.write(buffer,0, buffer.length);
                System.out.println(""+i++);
            } catch (IOException ex) {
                Logger.getLogger(player_thread.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        audio_out.close();
        audio_out.drain();
        System.err.println("Stopped");
    
    }
    
}
