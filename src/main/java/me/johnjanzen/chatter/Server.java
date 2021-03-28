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
        
        ArrayList<ClientConnector> threads= new ArrayList<ClientConnector>();
        while (true){
            try{
            Socket clientSock = s.accept();
            
            ClientConnector thread = new ClientConnector(clientSock);
            threads.add(thread);
            thread.start();
            System.out.print("IP CONNECTED");
            System.out.println(clientSock.getInetAddress());
            }
            catch (IOException e){
                e.printStackTrace();
            }
            
        }
        
        
        
        
        
        
    }
}
