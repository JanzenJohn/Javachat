/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.johnjanzen.chatter;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 *
 * @author john
 */
public class BitListener extends Thread{
    
    Socket mySocket;
    
    private ArrayList<NachrichtenListener> listeners = new ArrayList<>();
    
    public BitListener(Socket toListen) {
        mySocket = toListen;
    }
    public void addListener(NachrichtenListener h){
        listeners.add(h);
    }
    
    public void run(){
        while (true){
            try {
                mySocket.getInputStream().readNBytes(1);
            } catch (IOException ex) {}
            sendSignal();
        }
    }
    
    public void sendSignal(){
        for (NachrichtenListener h: listeners){
            h.checkForMessages();
        }
    }
}
