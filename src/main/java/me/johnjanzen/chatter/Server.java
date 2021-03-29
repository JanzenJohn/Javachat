/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.johnjanzen.chatter;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.Arrays;
import org.json.JSONObject;

/**
 *
 * @author john
 */
public class Server{
    
    public static void main(String[] args){
        ServerSocket s = null;
        try{
        int port = 9501;
        s = new ServerSocket();
        s.setReuseAddress(true);
        s.bind(new InetSocketAddress("", port));
        }
        catch (IOException e){ 
            e.printStackTrace();
            System.exit(1);
        }
        
        ArrayList<ClientConnector> threads= new ArrayList<>();
        ArrayList<PreConnector> waiting = new ArrayList<>();
        // I know this way of making ID's is stupid but I want to test first
        int preId = 1;
        while (true){
            try{
            Socket clientSock = s.accept();
            byte[] idBytes = clientSock.getInputStream().readNBytes(2);
            if (idBytes[0] == 0 && idBytes[1] == 0){
                PreConnector current = new PreConnector(clientSock, 1);
                waiting.add(current);
                clientSock.getOutputStream().write(new byte[]{0, (byte) preId});
                System.out.print("IP CONNECTED");
                System.out.println(clientSock.getInetAddress());
            }
            else{
                PreConnector toDelete = null;
                for(PreConnector cur: waiting){
                    if (cur.id == new BigInteger(1, idBytes).intValue()){
                        ClientConnector thread = new ClientConnector(cur.socket, clientSock);
                        threads.add(thread);
                        thread.start();
                        toDelete = cur;
                        System.out.println("ClientConnector Ready");
                    }
                }
                if (toDelete != null){
                    waiting.remove(toDelete);
                    System.out.println("Removed from Waiting list");
                }
            }
            
            }
            catch (IOException e){
                e.printStackTrace();
            }
            
        }
        
        
        
        
        
        
    }
}
