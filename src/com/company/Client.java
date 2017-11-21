package com.company;
import com.company.file.FileHandler;
import com.company.file.SerialFileAttr;

import java.net.*;
import java.io.*;
import java.util.HashSet;

public class Client extends NetworkProtocol implements Runnable
{
    private String serverName;
    private int port;

    public static void main(String [] args)
    {
        Client client = new Client("127.0.0.1");
        new Thread(client).start();
    }

    public Client()
    {
        this("0.0.0.0", 21, "external1" + File.separatorChar);
    }

    public Client(String serverName)
    {
        this(serverName, 21, "external1" + File.separatorChar);
    }

    public Client(String serverName, int port)
    {
        this(serverName, port, "external1" + File.separatorChar);
    }

    public Client(String serverName, int port, String directory)
    {
        this.serverName = serverName;
        this.port = port;
        this.directory = directory;
    }

    /**
     * Main function that gets called when the thread that has this Runnable class starts. This will synchronize the
     * files in their respective directories both ways.
     *
     * Operation is as follows:
     * 1) Get this computer's local files.
     * 2) Get the remote computer's local files.
     * 3) Compare the list of files and get the list of files we want to get from the remote computer.
     * 4) Send the list of files we want to pull to the remote computer, then pull the files.
     * 5) Compare the list of files again, but get the list of files we want to push.
     * 6) Send the list of files we want to push to the remote computer, then push the files.
     * 7) Local and remote files computers (only in the DIR's) are sync'ed.
     */
    @Override
    public void run()
    {
        // We're going to be instantiating client objects on demand per node.
        connectToServer();
        if(this.socket == null){
            return;
        }

        // Get this computer's local files and the remote computer's files and compare them to determine the files we want to pull.
        HashSet<SerialFileAttr> localFiles = FileHandler.getAllLocalFileInfo(directory);
        HashSet<SerialFileAttr> remoteFiles = receiveFileInfo();
        HashSet<SerialFileAttr> filesToPull = compare(localFiles, remoteFiles);

        pullAndCompareFiles(localFiles, remoteFiles);

        //pushCurrentFiles(localFiles, remoteFiles);

        // Cleanup
        closeSocket();
    }

    /**
     * Creates a Socket + makes a connection to the remote computer.
     */
    private void connectToServer(){
        try {
            System.out.println("Connecting to " + serverName  + " on port " + this.port);
            this.socket = new Socket(serverName, this.port);
            System.out.println("Just connected to " + socket.getRemoteSocketAddress());
        } catch(SocketTimeoutException s) {
            System.out.println("Socket timed out!");
        } catch(IOException e){
            System.out.println("Failed to connect to port");
            e.printStackTrace();
        }
    }
}