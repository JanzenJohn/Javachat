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
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author john
 */
public class ClientNetwork {
    Socket socket;
    InputStream in;
    OutputStream out;
    public ClientNetwork(String ip, int port) throws IOException{
        socket = new Socket();
        socket.connect(new InetSocketAddress(ip, port));
        
        in = socket.getInputStream();
        out = socket.getOutputStream();
        byte[] zero = {0, 0};
        out.write(zero);
       
    }
    public byte[] getId() throws IOException{
        byte[] id;
        id = in.readNBytes(2);
        return id;
    }
    public int sendMessage(Message message){
        byte[] headerLength;
        byte[] header;
        byte[] string = message.getText().getBytes();
        JSONObject j = new JSONObject();
        j.put("type", "sendall");
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
            simple_send(headerLength);
            simple_send(header);
            simple_send(string);
            return 0;
        }
        catch(IOException e){
            return 1;
        }
        
    }
    
    public Object recv(){
        try{
            
            int headerLength;
            byte[] lenBytes = simple_recv(2);
            
            headerLength = new BigInteger(1, lenBytes).intValue();
            byte[] headerBytes = simple_recv(headerLength);
            
            JSONObject header = new JSONObject(new String(headerBytes));
            
            String type = header.getString("type");
            byte[] data = simple_recv(header.getInt("length"));
            
            if(type.equals("string")){
                return new String(data);
            }
            else if(type.equals("message")){
                try{
                return new Message(header.getInt("senderId"), new String(data), header.getInt("recvId"));
                }
                catch (JSONException e){
                    e.printStackTrace();
                    System.out.println(header.keys());
                    return 2;
                }
            }
            else if(type.equals("JSON")){
                return header;
            }
            else{
                return 2;
            }
            
            
        }
        catch(IOException e){
            return 1;
        }
        
    }
    
    public void simple_send(byte[] bytes) throws IOException{
        out.write(bytes);
    }
    
    public byte[] simple_recv(int amount) throws IOException{
        return in.readNBytes(amount);
    }
    
    public int sendRecvSignal(){
        byte[] headerLength;
        byte[] header;
        byte[] string = ".".getBytes();
        JSONObject j = new JSONObject();
        j.put("type", "messageRequest");
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
            simple_send(headerLength);
            simple_send(header);
            simple_send(string);
            return 0;
        }
        catch(IOException e){
            return 1;
        }
    }
    
    
}
