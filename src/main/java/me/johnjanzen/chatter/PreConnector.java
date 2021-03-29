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
public class PreConnector {
    public int id;
    public Socket socket;
    public PreConnector(Socket s, int id){
        this.id = id;
        this.socket = s;
        
    }
}
