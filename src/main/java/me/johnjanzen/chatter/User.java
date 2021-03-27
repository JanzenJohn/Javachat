/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.johnjanzen.chatter;

import java.net.Socket;

/**
 *
 * @author john
 */
public class User {
    private Socket s;
    public int id;
    
    
    public User(int id, Socket s){
        this.id = id;
        this.s = s;
    }
    
    public Socket getSocket(){
        return s;
    }
}
