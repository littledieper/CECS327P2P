package com.company;
import java.net.*;
import java.io.*;

public class Client extends Thread
{
    private String serverName;
    private Socket client;
    private int port;

    public static void main(String [] args)
    {
        Thread t = new Client();
        t.start();
    }

    public Client()
    {
        this.serverName = "127.0.0.1";
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

    public void run()
    {
        String message = "This is CECS 327 Message";
        connectToServer();
        if(this.client == null){
            return;
        }
        //setOutputStreamContent(message);
        //System.out.println(getInputStreamContent());
        recieveFile("catrecieve.jpg");
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

    private String getInputStreamContent(){
        String message = null;
        DataInputStream in;
        try {
            in = new DataInputStream(this.client.getInputStream());

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
            out = new DataOutputStream(this.client.getOutputStream());
            out.writeUTF(message);
        }catch (IOException e) {
            System.out.println("No output stream found");
            e.printStackTrace();
        }
    }

    private boolean recieveFile(String pathName) {
        File file = new File(pathName);
        byte [] fileBytes = new byte[16*1024];

        try {
            file.createNewFile();
            InputStream inputStream = client.getInputStream();
            OutputStream fileWriter = new FileOutputStream(file);

            int count;
            while ((count = inputStream.read(fileBytes)) > 0) {
                fileWriter.write(fileBytes, 0, count);
            }

            fileWriter.close();
            inputStream.close();
        } catch (IOException e) {
            System.out.println("Client.recieveFile() failed");
            return false;
        }

        return true;
    } // end receiveFile()
}