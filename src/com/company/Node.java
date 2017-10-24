package com.company;
import java.net.*;
import java.io.*;
import java.util.LinkedList;
import java.util.List;

public class Node {
    Client clientMode;
    Server serverMode;
    List<String> nodeAddresses = new LinkedList<String>();
    int defaultPort = 21;

    private void FirstDiscovery(){

    }

    private void startServer(){
        serverMode = new Server(defaultPort, 0);
    }

    private String InitiateHandshake(String address, String message){
        return "";
    }

    private String ReplyToHandshake(){
        return "";
    }
}
