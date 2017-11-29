package com.company;

import com.company.file.SerialFileAttr;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

public abstract class NetworkProtocol {

    private static final int BUFFER_SIZE = 8 * 1024; // 8kb

    /**
     * Socket of the class that extends this.
     * Classes that extend + create objects of this should close this on cleanup.
     */
    protected Socket socket;

    /**
     * Receives a HashSet of SerialFileAttr's transferred over the network via a socket from the remote computer.
     * @return  All files in the remote PC's directory.
     *          NULL if passed something that's not the above container.
     */
    protected ArrayList<SerialFileAttr> receiveFileInfo() {
        ArrayList<SerialFileAttr> files = null;

        System.out.println("Receiving file info from remote...");
        try {
            ObjectInputStream in = new ObjectInputStream(this.socket.getInputStream());

            // Read the object from the stream and make sure its a HashSet.
            Object receivedObject = in.readObject();
            if (receivedObject instanceof ArrayList)
                files = (ArrayList<SerialFileAttr>) receivedObject;

        } catch (IOException e) {
            System.out.println("receiveFileInfo() failed. Could be expected if client wasn't doing an initial run.");
            return new ArrayList<>();
            //e.printStackTrace();
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

        System.out.println("Sending file info to remote...");
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
            OutputStream fileWriter = new BufferedOutputStream(new FileOutputStream(file));

            // Receive the file from the stream.
            int count;
            long fileSize = fileToReceive.getSize();
            while (fileSize > 0 &&
                    (count = inputStream.read(fileBytes, 0, (int) Math.min(fileBytes.length, fileSize))) > 0) {
                fileWriter.write(fileBytes, 0, count);
                fileWriter.flush();
                fileSize -= count;
            }

            System.out.println(fileToReceive.getName() + " successfully received into " + directory);

            // Cleanup
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
            OutputStream outputStream = new BufferedOutputStream(this.socket.getOutputStream());

            // Send the bytes of the file over the stream.
            int count = 0;
            long fileSize = fileToSend.getSize();
            while ((count = fileReader.read(fileBytes, 0, (int) Math.min(fileBytes.length, fileSize))) > 0) {
                outputStream.write(fileBytes, 0, count);
                fileSize -= count;
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
     * From the first argument 'oldList', compare the list with the 2nd argument 'newList'
     * The result of this comparison should include all files from 'newList' that do not exist or are newer than
     * the same file in 'oldList'.
     *
     * @param oldList HashSet of SerialFileAttr's that belong to the 'local' computer.
     * @param newList HashSet of SerialFileAttr's that belong to the 'remote' computer
     * @return  HashSet of SerialFileAttr's of files to pull from remote computer to local computer.
     */
    protected ArrayList<SerialFileAttr> compare(ArrayList<SerialFileAttr> oldList, ArrayList<SerialFileAttr> newList) {
        ArrayList<SerialFileAttr> filesToReturn = new ArrayList<>();

        System.out.println("Comparing files...");
        for ( SerialFileAttr remoteFile : newList) {
            if (!oldList.contains(remoteFile)) {
                // if this computer doesn't have the remote file, then just pull it.
                filesToReturn.add(remoteFile);
            } else {
                // this computer does contain a file that the remote computer has, so find the newer one and pull it
                for ( SerialFileAttr localFile : oldList) {
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
