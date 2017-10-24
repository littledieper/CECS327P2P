package com.company;

import java.io.File;
import java.io.Serializable;
import java.nio.file.attribute.BasicFileAttributes;

public class SerialFileAttr implements Serializable {

    private String name;
    private String lastModifiedTime;
    private long size;

    public SerialFileAttr(File file, BasicFileAttributes fileAttr){
        this.name = file.getName();
        this.lastModifiedTime = fileAttr.lastModifiedTime().toString();
        this.size = fileAttr.size();
    }

    public SerialFileAttr(String file, BasicFileAttributes fileAttr){
        this.name = file;
        this.lastModifiedTime = fileAttr.lastModifiedTime().toString();
        this.size = fileAttr.size();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastModifiedTime() {
        return lastModifiedTime;
    }

    public void setLastModifiedTime(String lastModifiedTime) {
        this.lastModifiedTime = lastModifiedTime;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append(
                "Name: "+name+"\n"+
                "LMT: "+lastModifiedTime+"\n"+
                "size: "+size
        );
        return sb.toString();
    }
}
