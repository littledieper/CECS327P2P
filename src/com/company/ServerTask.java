package com.company;

import com.company.file.FileHandler;
import com.company.file.SerialFileAttr;

import java.io.File;
import java.net.Socket;
import java.util.ArrayList;

/**
 * Dispatched thread when Server accepts a connection.
 */
public class ServerTask extends NetworkProtocol implements Runnable
{
    /** Is this the initial run? */
    private boolean initialRun;

    /**
     * Create ServerTask that operates on a Socket connection.
     * @param socket    socket (connection) to a computer in the network.
     */
    public ServerTask(Socket socket, boolean initialRun) {
        this.socket = socket;
        this.initialRun = initialRun;
    }

    /**
     * Main function that gets called when the ServerSocket dispatches the work to the created thread.
     * This function should be called in the same, but opposite operation from the send and receive calls in Client.java
     *
     * Please see Client.run() for the full steps of file synchronization.
    */
    @Override
    public void run() {

		/*
        // Receive the files that the client thinks it needs and send it over.
        HashSet<SerialFileAttr> filesToPush = receiveFileInfo();
        for (SerialFileAttr fileToPush: filesToPush) {
            sendFile(fileToPush);
        */
        String localDir = "external" + File.separatorChar; //getLocalDirectory();

        // Send this computer's local files to the remote PC.
        sendFileInfo(FileHandler.getAllLocalFileInfo(localDir));

        // Receive the files that the client wants to push to this computer and receive them.
        ArrayList<SerialFileAttr> filesToPull = receiveFileInfo();
        for (SerialFileAttr fileToPull: filesToPull) {
            receiveFile(localDir, fileToPull);
        }

        if (initialRun) {
            // Receive the files that the client thinks it needs and send it over.
            ArrayList<SerialFileAttr> filesToPush = receiveFileInfo();
            for (SerialFileAttr fileToPush: filesToPush) {
                sendFile(getRemoteDirectory(this.socket) + File.separatorChar, fileToPush);
            }
        }

        // Cleanup
        closeSocket();
    }

}
