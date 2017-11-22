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

    public SerialFileAttr(String directory, String file, BasicFileAttributes fileAttr) {
        this.name = file;
        this.lastModifiedTime = fileAttr.lastModifiedTime().toInstant();
        this.size = fileAttr.size();
        this.md5 = FileHandler.getMD5(new File (directory + file));
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

        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        return md5 != null ? md5.equals(that.md5) : that.md5 == null;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (md5 != null ? md5.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "SerialFileAttr{" +
                "name='" + name + '\'' +
                ", md5='" + md5 + '\'' +
                ", lastModifiedTime=" + lastModifiedTime +
                ", size=" + size +
                "}\n";
    }

    public boolean isNewer(SerialFileAttr localFile) {
        // Make sure we're dealing with the same file
        if (!this.equals(localFile)) {
            return false;
        }

        return this.lastModifiedTime.compareTo(localFile.getLastModifiedTime()) > 0;
    }
}
