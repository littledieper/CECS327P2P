package com.company;
import java.net.*;
import java.io.*;
public class Server extends Thread
{
    private ServerSocket serverSocket;
    private Socket server;
    private int port;
    private int timeout;

    public static void main(String[] args){
        Thread t = new Server();
        t.start();
    }

    public Server()
    {
        this.port = 21;
        this.timeout = 10000;
    }

    public Server(int port)
    {
        this.port = port;
        this.timeout = 10000;
    }

    public Server(int port, int timeout)
    {
        this.port = port;
        this.timeout = timeout;
    }

    public void run()
    {

        while(true)
        {
            String message = "This is CECS 327 Message";
            makeServerSocket();
            connectToClient();
            if(this.server == null){
                break;
            }
            System.out.println(getInputStreamContent());
            setOutputStreamContent(message);
            closeServer();
        }
    }

    private void makeServerSocket(){
        try {
            this.serverSocket = new ServerSocket(this.port);
        } catch (IOException e) {
            System.out.println("Cannot make a server socket at port " + this.port);
            e.printStackTrace();
        }
        try {
            this.serverSocket.setSoTimeout(this.timeout);
        } catch (SocketException e) {
            System.out.println("Error with timeout");
            e.printStackTrace();
        }
    }

    private void connectToClient(){
        this.server = null;
        try {
            System.out.println("Waiting for server on port " +
                    this.serverSocket.getLocalPort() + "...");
            this.server = this.serverSocket.accept();

            System.out.println("Just connected to "
                    + this.server.getRemoteSocketAddress());

        } catch(SocketTimeoutException s)
        {
            System.out.println("Socket timed out!");
        } catch(IOException e){
            System.out.println("Failed to connect to port");
            e.printStackTrace();
        }
    }

    private void closeServer(){
        try{
            this.server.close();
        } catch(IOException e){
            System.out.println("Client failed to close or is null");
            e.printStackTrace();
        }
    }

    private String getInputStreamContent(){
        String message = null;
        DataInputStream in;
        try {
            in = new DataInputStream(this.server.getInputStream());

            message = in.readUTF();
        }catch (IOException e) {
            System.out.println("No input stream found");
            e.printStackTrace();
        }

        return message;
    }

    private void setOutputStreamContent(String message){
        DataOutputStream out;
        try {
            out = new DataOutputStream(this.server.getOutputStream());
            out.writeUTF(message);
        }catch (IOException e) {
            System.out.println("No output stream found");
            e.printStackTrace();
        }
    }
}