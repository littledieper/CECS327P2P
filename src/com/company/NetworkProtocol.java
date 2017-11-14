package com.company;

import java.io.*;
import java.nio.file.attribute.BasicFileAttributes;

public abstract class NetworkProtocol {

    protected String recieveMessage(DataInputStream in, String message) throws IOException{
        message = in.readUTF();
        return message;
    }

    protected void sendMessage(DataOutputStream out, String message) throws IOException{
        out.writeUTF(message);
    }

    protected FileTransfer recieveFileTransfer(ObjectInputStream in) throws IOException, ClassNotFoundException{
        FileTransfer ft;
        Object inObject = in.readObject();
        if(inObject instanceof FileTransfer) ft = (FileTransfer)in.readObject();
        else ft = null;

        return ft;
    }

    protected void sendFileTransfer(ObjectOutputStream out, FileTransfer ft) throws IOException{
        out.writeObject(ft);
    }

    protected void recieveFile(InputStream inputStream, long fileSize, String pathName) throws IOException {
        File file = new File(pathName);

        byte[] fileBytes = new byte[(int)fileSize];
        int count = 0;

        file.createNewFile();
        OutputStream fileWriter = new FileOutputStream(file);


        while ((count = inputStream.read(fileBytes)) > 0) {
            fileWriter.write(fileBytes, 0, count);
        }
        fileWriter.close();
    }

    protected void sendFile(OutputStream outputStream, String pathName) throws IOException{
        File file = new File(pathName);
        byte [] fileBytes = new byte[(int) file.length()];
        InputStream fileReader = new FileInputStream(file);

        outputStream.write(fileBytes);


        fileReader.close();
    }

    protected FileTransfer InputFileAttr(ObjectInputStream in){
        FileTransfer ft;

        try {
            ft = (FileTransfer)in.readObject();

            return ft;
        }catch (Exception e) {
            System.out.println("No input stream found");
            e.printStackTrace();
        }
        return null;
    }

    protected void OutputFileAttr(ObjectOutputStream out, FileTransfer ft){
        try {
            out.writeObject(ft);
        }catch (IOException e) {
            System.out.println("No output stream found");
            e.printStackTrace();
        }
    }


}
