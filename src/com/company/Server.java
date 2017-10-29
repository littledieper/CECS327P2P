package com.company;
import java.net.*;
import java.io.*;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;

public class Server extends Thread
{
    private ServerSocket serverSocket;
    private Socket server;
    private int port;
    private int timeout;

    public static void main(String[] args){
        Thread t = new Server(21, 60000);
        t.start();
    }

    public Server()
    {
        this.port = 21;
        this.timeout = 10000;
        makeServerSocket();
    }

    public Server(int port)
    {
        this.port = port;
        this.timeout = 10000;
        makeServerSocket();
    }

    public Server(int port, int timeout)
    {
        this.port = port;
        this.timeout = timeout;
        makeServerSocket();
    }

    public void run()
    {
        FileTransfer ft;
        while(true)
        {
            //Lock
            connectToClient();
            if(this.server == null){
                break;
            }
            //transfer names and metadata
            try{
                ft = new FileTransfer();
                ft.listFiles();
                OutputFileAttr(ft);
                if(ft.toString() == null)
                    System.out.println("empty");
                else
                    System.out.println(ft.toString());
            }catch(Exception e){System.out.println("cannot open path server");}


            //synchronize files

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

            // <alex> 23-Oct-2017
            // Temporarily close the serverSocket while we're dealing with the connection. When we close the socket
            // using closeServer(), we'll reopen it.
            if (this.server == null) {
                this.server = this.serverSocket.accept();
                serverSocket.close();
            }

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

            // <alex> 23-Oct-2017
            // Reopen the serverSocket once we're finished with the Socket to the client.
            if (serverSocket.isClosed())
                makeServerSocket();

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

    private void OutputFileAttr(FileTransfer ft){
        ObjectOutputStream out;
        try {
            out = new ObjectOutputStream(this.server.getOutputStream());
            out.writeObject(ft);
        }catch (IOException e) {
            System.out.println("No output stream found");
            e.printStackTrace();
        }
    }


    private boolean sendFile(String pathName) {
        File file = new File(pathName);
        byte [] fileBytes = new byte[(int) file.length()];

        try {
            InputStream fileReader = new FileInputStream(file);
            OutputStream outputStream = server.getOutputStream();

            int count;
            while ((count = fileReader.read(fileBytes)) > 0) {
                outputStream.write(fileBytes, 0, count);
            }

            fileReader.close();
            outputStream.close();
        } catch (IOException e) {
            System.out.println("Server.sendFile() failed");
            return false;
        }

        return true;
    }
}