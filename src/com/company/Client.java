package com.company;
import com.company.file.FileHandler;
import com.company.file.SerialFileAttr;

import java.net.*;
import java.io.*;
import java.util.ArrayList;

public class Client extends NetworkProtocol implements Runnable
{
    private String serverName;
    private int port;
    private boolean initialRun;

    public static void main(String [] args)
    {
        Client client = new Client("127.0.0.1", 27016, true);
        new Thread(client).start();
    }

    public Client()
    {
        this("0.0.0.0", 21);
    }

    public Client(String serverName)
    {
        this(serverName, 21);
    }

    public Client(String serverName, boolean initialRun) {
        this(serverName, 21, initialRun);
    }

    public Client(String serverName, int port)
    {
        this(serverName, port, false);
    }

    public Client(String serverName, int port, boolean initialRun) {
        this.serverName = serverName;
        this.port = port;
        this.initialRun = initialRun;
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

        // Get the directory paths.
        String localDir = getLocalDirectory();
        String remoteDir = getRemoteDirectory(this.socket);

        // Get this computer's list of files that we want to send over and get the list of files that 'belong' to
        // this PC that are saved on the remote PC.
        ArrayList<SerialFileAttr> localFiles = FileHandler.getAllLocalFileInfo(localDir);
        sendFileInfo(localFiles);        // Send the full list of files over to the remote PC so it can cleanup any leftover files.
        ArrayList<SerialFileAttr> remoteCopyOfLocalFiles = receiveFileInfo();

        // Compare the two lists, and get information on any new files or newer versions.
        // Send the info about the list of files we want to push, then push the actual files over.
        ArrayList<SerialFileAttr> filesToPush = compare(remoteCopyOfLocalFiles, localFiles);
        sendFileInfo(filesToPush);
        for (SerialFileAttr fileToPush : filesToPush) {
            sendFile(localDir, fileToPush);
        }

        if (initialRun) {
            // Now we're working on pulling from the remote PC.
            // So, grab the list of files that 'belong' to the remote PC that are saved on locally on this PC and
            // then grab the list of files that are currently saved on the remote PC.
            ArrayList<SerialFileAttr> localCopyOfRemoteFiles = FileHandler.getAllLocalFileInfo(remoteDir);
            ArrayList<SerialFileAttr> remoteFiles = receiveFileInfo();

            // Compare the two lists, and get information on any new files or newer versions from that remote PC that we want.
            // Then, receive them as the remote PC should be sending them over at this time.
            ArrayList<SerialFileAttr> filesToPull = compare(localCopyOfRemoteFiles, remoteFiles);
            sendFileInfo(filesToPull);
            for (SerialFileAttr fileToPull : filesToPull) {
                receiveFile(remoteDir, fileToPull);
            }

            // If we pulled files, make sure to clean the directory of anything that doesn't exist on remote.
            FileHandler.cleanup(remoteDir, remoteFiles);
        }

        /*
        // Get the directories.
        String localDir = getLocalDirectory();
        String remoteDir = getRemoteDirectory(this.socket);

        // Get this computer's local files and the remote computer's files and compare them to determine the files we want to pull.
        ArrayList<SerialFileAttr> localFiles = FileHandler.getAllLocalFileInfo(localDir);
        ArrayList<SerialFileAttr> remoteFiles = receiveFileInfo();

        // Now we work on pushing the local files to remote computer, so compare to get list of files to push.
        // We don't want (nor do we care about) the local changes made to the local directory after we pulled updated files.
        ArrayList<SerialFileAttr> filesToPush = compare(remoteFiles, localFiles);
        sendFileInfo(filesToPush);
        for (SerialFileAttr fileToPush: filesToPush) {
            sendFile(localDir, fileToPush);
        }

        if (initialRun) {
            ArrayList<SerialFileAttr> filesToPull = compare(localFiles, remoteFiles);
            // Let the remote PC know which files we want (as comparison is a local process) and pull the files into the local directory.
            sendFileInfo(filesToPull);
            for (SerialFileAttr fileToPull: filesToPull) {
                receiveFile(remoteDir, fileToPull);
            }
        }
        */
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