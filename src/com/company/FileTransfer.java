package com.company;
import java.net.*;
import java.io.*;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;

public class FileTransfer implements Serializable {
    private ArrayList<SerialFileAttr> fileAttr;
    private String path;

    public FileTransfer() throws IOException {
        this.path = "external"+File.separatorChar;
        fileAttr = new ArrayList<SerialFileAttr>();
        this.listFiles();
    }

    public FileTransfer(String path) throws IOException {
        this.path = path;
        fileAttr = new ArrayList<SerialFileAttr>();
        this.listFiles();
    }

    public static void main(String[] args){
        FileTransfer ft;
        try{
            ft = new FileTransfer();
            System.out.println(ft.toString());
        }
        catch(IOException e){System.out.println("Cannot open path");}

    }

    public void listFiles() throws IOException {
        Files.walkFileTree(Paths.get(this.path), new SimpleFileVisitor<Path>() {
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                Path getPath = Paths.get(path);
                String relPath = file.toAbsolutePath().toString().substring(getPath.toAbsolutePath().toString().length()+1);
                fileAttr.add(new SerialFileAttr(relPath, attrs));
                return FileVisitResult.CONTINUE;
            }
        });
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (SerialFileAttr s : fileAttr) {
            sb.append(s.toString()+"\n");
        }
        return sb.toString();
    }
}