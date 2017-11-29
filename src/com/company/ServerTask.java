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
    /**
     * Create ServerTask that operates on a Socket connection.
     * @param socket    socket (connection) to a computer in the network.
     */
    public ServerTask(Socket socket) {
        this.socket = socket;
    }

    /**
     * Main function that gets called when the ServerSocket dispatches the work to the created thread.
     * This function should be called in the same, but opposite operation from the send and receive calls in Client.java
     *
     * Please see Client.run() for the full steps of file synchronization.
    */
    @Override
    public void run() {
        String localDir = getLocalDirectory(); //"external" + File.separatorChar;
        String remoteDir = getRemoteDirectory(this.socket);

        // Get the full list of files from the remote PC so we can cleanup later if necessary.
        ArrayList<SerialFileAttr> remoteFiles = receiveFileInfo();

        // Get the list of files from the remote PC's directory (saved locally) and send it over.
        ArrayList<SerialFileAttr> localFilesOfRemoteFiles = FileHandler.getAllLocalFileInfo(remoteDir);
        sendFileInfo(localFilesOfRemoteFiles);

        // Receive the list of files the remote PC is going to push to this PC, then start receiving the files
        // as the remote PC should be sending them over at this time.
        ArrayList<SerialFileAttr> filesToPull = receiveFileInfo();
        for (SerialFileAttr fileToPull : filesToPull) {
            receiveFile(remoteDir, fileToPull);
        }

        // If the client is not running an initial run, the socket is likely closed at this time
        // so we might as well test for it.
        if (!this.socket.isClosed()) {
            // Now we're working on pushing local files to the remote PC.
            // So, grab the list of files from this PC's local directory and send it over.
            ArrayList<SerialFileAttr> localFiles = FileHandler.getAllLocalFileInfo(localDir);
            sendFileInfo(localFiles);

            // Receive the list of files that the remote PC wants from this PC, then start to push the files.
            ArrayList<SerialFileAttr> filesToPush = receiveFileInfo();
            for (SerialFileAttr fileToPush : filesToPush) {
                sendFile(localDir, fileToPush);
            }
        }
        /*
        // Send this computer's local files to the remote PC.
        sendFileInfo(FileHandler.getAllLocalFileInfo(localDir));

        // Receive the files that the client wants to push to this computer and receive them.
        ArrayList<SerialFileAttr> filesToPull = receiveFileInfo();
        for (SerialFileAttr fileToPull: filesToPull) {
            receiveFile(remoteDir, fileToPull);
        }

        // Receive the files that the client thinks it needs and send it over.
        ArrayList<SerialFileAttr> filesToPush = receiveFileInfo();
        for (SerialFileAttr fileToPush: filesToPush) {
            sendFile(localDir, fileToPush);
        }

        */
        // Cleanup
        FileHandler.cleanup(remoteDir, remoteFiles);
        closeSocket();
    }

}
