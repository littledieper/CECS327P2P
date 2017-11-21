package com.company;

import com.company.file.SerialFileAttr;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

public abstract class NetworkProtocol {

    private static final int BUFFER_SIZE = 8196; // 8kb

    /**
     * Socket of the class that extends this.
     * Classes that extend + create objects of this should close this on cleanup.
     */
    protected Socket socket;

    protected String recieveMessage() {
        if (this.socket == null)
            return "";

        try {
            DataInputStream in = new DataInputStream(this.socket.getInputStream());
            return in.readUTF();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "";
    }

    protected void sendMessage(String message) {
        if (this.socket == null)
            return;

        try {
            DataOutputStream out = new DataOutputStream(this.socket.getOutputStream());
            out.writeUTF(message);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Receives a HashSet of SerialFileAttr's transferred over the network via a socket from the remote computer.
     * @return  All files in the remote PC's directory.
     *          NULL if passed something that's not the above container.
     */
    protected ArrayList<SerialFileAttr> receiveFileInfo() {
        ArrayList<SerialFileAttr> files = null;

        try {
            ObjectInputStream in = new ObjectInputStream(this.socket.getInputStream());

            // Read the object from the stream and make sure its a HashSet.
            Object receivedObject = in.readObject();
            if (receivedObject instanceof ArrayList)
                files = (ArrayList<SerialFileAttr>) receivedObject;

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return files;
    }

    /**
     * Sends a ArrayList of SerialFileAttr's transferred over the network via a socket to the remote computer.
     * @param files     files to send over the network.
     */
    protected void sendFileInfo(ArrayList<SerialFileAttr> files) {
        if (files == null)
            return;

        try {
            ObjectOutputStream out = new ObjectOutputStream(this.socket.getOutputStream());
            out.writeObject(files);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Receive's a file from the remote computer and places it into the given directory (relative, abs may work).
     * @param fileToReceive File received from the remote PC.
     */
    protected void receiveFile(String directory, SerialFileAttr fileToReceive) {
        // Create the file + allocate the buffer in an array
        File file = new File(directory + fileToReceive.getName());
        byte [] fileBytes = new byte[BUFFER_SIZE]; // buffer size

        try {
            // if the parent directory doesn't exist, make it
            if (!file.getParentFile().exists())
                file.getParentFile().mkdirs();

            // If it doesn't exist, create the destination file.
            file.createNewFile();

            // Open the socket's input stream to read in the file from the network and open the output stream to the
            // file so we can write it to disk.
            InputStream inputStream = this.socket.getInputStream();
            OutputStream fileWriter = new FileOutputStream(file);

            // Receive the file from the stream.
            int count;
            long fileSize = fileToReceive.getSize();
            while (fileSize > 0 &&
                    (count = inputStream.read(fileBytes, 0, (int) Math.min(fileBytes.length, fileToReceive.getSize()))) > 0) {
                fileWriter.write(fileBytes, 0, count);
                fileSize -= count;
            }

            System.out.println(fileToReceive.getName() + " successfully received into " + directory);

            // Cleanup
            fileWriter.flush();
            fileWriter.close();
        } catch (IOException e) {
            System.out.println("receiveFile() failed");
            e.printStackTrace();
        }
    }

    protected void sendFile(String directory, SerialFileAttr fileToSend) {
        // Get the file + allocate the buffer in an array.
        File file = new File(directory + fileToSend.getName());
        byte [] fileBytes = new byte[BUFFER_SIZE]; // buffer size

        try {
            // Open the file input stream (to read in the file) and open the output stream from the socket
            // so we can send the file over the network.
            InputStream fileReader = new FileInputStream(file);
            OutputStream outputStream = this.socket.getOutputStream();

            // Send the bytes of the file over the stream.
            int count = 0;
            while ((count = fileReader.read(fileBytes, 0, (int) Math.min(fileBytes.length, fileToSend.getSize()))) > 0) {
                outputStream.write(fileBytes, 0, count);
            }

            System.out.println(fileToSend.getName() + " successfully sent to " + directory);

            // Cleanup
            outputStream.flush();
            fileReader.close();
        } catch (IOException e) {
            System.out.println("sendFile() failed");
            e.printStackTrace();
        }
    }

    /**
     * From the first argument ('local files'), compare the list with the 2nd argument ('remote files').
     * The result of this comparison should include all files from 'remote' that do not exist or are newer than
     * the same file in 'local'.
     *
     * @param localFiles HashSet of SerialFileAttr's that belong to the 'local' computer.
     * @param remoteFiles HashSet of SerialFileAttr's that belong to the 'remote' computer
     * @return  HashSet of SerialFileAttr's of files to pull from remote computer to local computer.
     */
    protected ArrayList<SerialFileAttr> compare(ArrayList<SerialFileAttr> localFiles, ArrayList<SerialFileAttr> remoteFiles) {
        ArrayList<SerialFileAttr> filesToReturn = new ArrayList<>();

        for ( SerialFileAttr remoteFile : remoteFiles) {
            if (!localFiles.contains(remoteFile)) {
                // if this computer doesn't have the remote file, then just pull it.
                filesToReturn.add(remoteFile);
            } else {
                // this computer does contain a file that the remote computer has, so find the newer one and pull it
                for ( SerialFileAttr localFile : localFiles) {
                    // Find the same, but newer file.
                    if (remoteFile.isNewer(localFile)) {
                        filesToReturn.add(remoteFile);
                        break;
                    }
                } // end for
            } // end if/else
        }

        return filesToReturn;
    }

    protected String getRemoteDirectory(Socket socket) {
        return socket.getInetAddress().getCanonicalHostName() + File.separatorChar;
    }

    public static String getLocalDirectory() {
        String localHostname = "";

        try {
            localHostname = InetAddress.getLocalHost().getHostName() + File.separatorChar;
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        return localHostname;
    }

    /**
     * Closes the socket associated with this instance.
     */
    protected void closeSocket() {
        if (this.socket == null)
            return;

        try {
            this.socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
