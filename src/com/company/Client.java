package com.company;
import java.net.*;
import java.io.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class Client extends NetworkProtocol implements Runnable
{
    private String serverName;
    private Socket client;
    private int port;
    private String threadMessage;

    public static void main(String [] args)
    {
    }

    public Client()
    {
        this.serverName = "0.0.0.0";
        this.port = 21;
    }

    public Client(String serverName)
    {
        this.serverName = serverName;
        this.port = 21;
    }

    public Client(String serverName, int port)
    {
        this.serverName = serverName;
        this.port = port;
    }

    public Client(String serverName, int port, String message)
    {
        this.serverName = serverName;
        this.port = port;
        this.threadMessage = message;
    }

    public void run()
    {
        connectToServer();
        if(this.client == null){
            return;
        }


        FileTransfer clientFT;

        try{
            ObjectInputStream in = new ObjectInputStream(client.getInputStream());
            FileTransfer ft = InputFileAttr(in);
            if(ft != null)
                clientFT = new FileTransfer();
            System.out.println("Null object");
            in.close();
        }catch(Exception e){System.out.println(e.toString());}

        closeClient();
    }

    private void connectToServer(){
        this.client = null;
        try {
            System.out.println("Connecting to " + serverName
                    + " on port " + this.port);
            this.client = new Socket(serverName, this.port);
            System.out.println("Just connected to "
                    + client.getRemoteSocketAddress());
        } catch(SocketTimeoutException s) {
            System.out.println("Socket timed out!");
        } catch(IOException e){
            System.out.println("Failed to connect to port");
            e.printStackTrace();
        }
    }

    private void closeClient(){
        try{
            this.client.close();
        } catch(IOException e){
            System.out.println("Client failed to close or is null");
            e.printStackTrace();
        }
    }


}