/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.johnjanzen.chatter;

/**
 *
 * @author john
 */
public class Message {
    private int senderId;
    private String message;
    private int recvId;
    
    public Message(int senderId, String message, int revId){
        this.senderId = senderId;
        this.message = message;
        this.recvId = revId;
    }
    
    public int getSenderId(){
        return this.senderId;
    }
    
    public int getRecvId(){
        return this.recvId;
    }
    
    public String getText(){
        return this.message;
    }
}
