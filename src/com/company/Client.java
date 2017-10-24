package com.company;
import java.net.*;
import java.io.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class Client extends Thread
{
    private String serverName;
    private Socket client;
    private int port;
    private String threadMessage;

    public static void main(String [] args)
    {
        Thread t = new Client("0.0.0.0",21,"thread1");
        t.start();
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
        FileTransfer ft = InputFileAttr();
        FileTransfer clientFT;
        if(ft == null)
            System.out.println("Null object");
        else{
            try{
                clientFT = new FileTransfer();
            }catch(Exception e){}
        }
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

    private FileTransfer InputFileAttr(){
        ObjectInputStream in;
        FileTransfer ft;

        try {
            in = new ObjectInputStream(this.client.getInputStream());
            ft = (FileTransfer)in.readObject();

            return ft;
        }catch (Exception e) {
            System.out.println("No input stream found");
            e.printStackTrace();
        }
        return null;
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