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
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONObject;

/**
 *
 * @author john
 */
public class ClientConnector extends Thread{
    private User user;
    static volatile ArrayList<ClientConnector> threads;
    private ArrayList<Message> messages;
    InputStream in;
    OutputStream out;
    
    public ClientConnector(Socket s) throws IOException{
        super();
        
        user = new User(1, s);
        in = user.getSocket().getInputStream();
        out = user.getSocket().getOutputStream();
        messages = new ArrayList<>();
        try{
        threads.add(this);
        }
        catch(NullPointerException e){
        threads = new ArrayList<>();
        threads.add(this);
        }
        
        while(true){
            int amount = 0;
            for (ClientConnector c: threads){
                if(c.getUserId() == user.id)amount++;
            }
            if(amount == 1)break;
            
            user.id = (int) ((Math.random() * (99999 - 1)) + 1);
        }
        
        
        
    
    }
    
    public int getUserId(){
        return user.id;
    }
    
    private void sendStatus(int code, String message){
        byte[] headerLength;
        byte[] header;
        byte[] data = {0};
        
        JSONObject j= new JSONObject();
        j.put("type", "JSON");
        j.put("status", code);
        j.put("message", message);
        j.put("length", data.length);
        header = j.toString().getBytes();
        headerLength = BigInteger.valueOf(header.length).toByteArray();
        if(headerLength.length == 1){
            byte[] temp = new byte[2];
            temp[0] = 0;
            temp[1] = headerLength[0];
            headerLength = temp;
        }
        
        try{
            out.write(headerLength);
            out.write(header);
            out.write(data);
        }
        catch(IOException e){}
    }
    private int sendMessage(Message message){
        byte[] headerLength;
        byte[] header;
        byte[] string = message.getText().getBytes();
        JSONObject j = new JSONObject();
        j.put("type", "message");
        j.put("senderId", message.getSenderId());
        j.put("recvId", message.getRecvId());
        j.put("length", string.length);
        header = j.toString().getBytes();
        headerLength = BigInteger.valueOf(header.length).toByteArray();
        if (headerLength.length > 2){
            return 2;
        }
        else if(headerLength.length == 1){
            byte[] temp = new byte[2];
            temp[0] = 0;
            temp[1] = headerLength[0];
            headerLength = temp;
        }
        try{
            out.write(headerLength);
            out.write(header);
            out.write(string);
            return 0;
        }
        catch(IOException e){
            return 1;
        }
        
    }
    
    public void addToQueue(Message message){
        messages.add(message);
    }
    
    public void run(){
        Object[] cs = threads.toArray();
        for (Object c : cs){
            ClientConnector x = (ClientConnector) c;
            if(!x.isAlive()){
                threads.remove(x);
            }
        }
        
        while (true){
            byte[] lenBytes;
            byte[] headerBytes;
            byte[] dataBytes;
            String headerString;
            boolean m = false;
            try{
                
                lenBytes = in.readNBytes(2);
                int headerLength = new BigInteger(1, lenBytes).intValue();
                headerBytes = in.readNBytes(headerLength);
                headerString = new String(headerBytes);
                JSONObject header = new JSONObject(headerString);
                int dataLength = header.getInt("length");
                dataBytes = in.readNBytes(dataLength);
                
                if (header.getString("type").equals("sendall")){
                    for (ClientConnector c : threads){
                        c.addToQueue(new Message(user.id, new String(dataBytes), 0));
                    }
                    sendStatus(200, "OK");
                }
                else if (header.getString("type").equals("messageRequest")){
                    m = true;
                }
                
                
                
                
                
                
            }
            catch(IOException e){
                try {
                    in.close();
                } catch (IOException ex) {
                    System.err.println("Client Vanished");
                }
                try{
                out.close();
                } catch (IOException ex) {
                    System.err.println("Client Vanished");
                    
                }
                break;
                
            }
            
            
            
            if(m)
                {   
                    
                for (Message message : messages){
                    sendMessage(message);
                
                }
                while(!messages.isEmpty()){
                    messages.remove(0);
                }
                sendStatus(200, "OK");
            
            }
        }
    }
    
}
