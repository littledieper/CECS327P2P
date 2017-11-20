package com.company.file;

import java.io.File;
import java.io.Serializable;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.Instant;

public class SerialFileAttr implements Serializable {

    /** Name of the file. */
    private String name;
    /** MD5 hash of the file. */
    private String md5;
    /** Last modified time of the file. */
    private Instant lastModifiedTime;
    /** Size (in bytes) of the file */
    private long size;

    public SerialFileAttr(File file, BasicFileAttributes fileAttr){
        this.name = file.getName();
        this.lastModifiedTime = fileAttr.lastModifiedTime().toInstant();
        this.size = fileAttr.size();
        this.md5 = FileHandler.getMD5(file);
    }

    public SerialFileAttr(String file, BasicFileAttributes fileAttr){
        this.name = file;
        this.lastModifiedTime = fileAttr.lastModifiedTime().toInstant();
        this.size = fileAttr.size();
        this.md5 = FileHandler.getMD5(new File (file));
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Instant getLastModifiedTime() {
        return lastModifiedTime;
    }

    public void setLastModifiedTime(Instant lastModifiedTime) {
        this.lastModifiedTime = lastModifiedTime;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SerialFileAttr that = (SerialFileAttr) o;

        if (size != that.size) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        return md5 != null ? md5.equals(that.md5) : that.md5 == null;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (md5 != null ? md5.hashCode() : 0);
        result = 31 * result + (int) (size ^ (size >>> 32));
        return result;
    }

    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append(
                "Name: "+name+"\n"+
                "LMT: " + lastModifiedTime+"\n"+
                "size: "+size + "\n" +
                "MD5: "+md5+"\n"
        );
        return sb.toString();
    }

    public boolean isNewer(SerialFileAttr localFile) {
        // Make sure we're dealing with the same file
        if (!this.equals(localFile)) {
            return false;
        }

        return this.lastModifiedTime.compareTo(localFile.getLastModifiedTime()) > 0;
    }
}
