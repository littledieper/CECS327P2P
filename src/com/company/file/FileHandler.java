package com.company.file;

import java.io.*;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

public class FileHandler {

    /** Execution location of this file. */
    public static String execLocation = FileHandler.class.getProtectionDomain().getCodeSource().getLocation().getPath();

    /** Static singnleton for MessageDigest to compute MD5 hashes. */
    private static MessageDigest md;

    /**
     * Returns the singleton MessageDigest object. Creates one if it doesn't exist.
     * @return MessageDigest singleton
     */
    private static MessageDigest getMessageDigest() {
        if (md == null) {
            try {
                md = MessageDigest.getInstance("MD5");
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
        }

        return md;
    }

    /**
     * Returns the MD5 hash of the specified file.
     * @param file  file to compute MD5 hash of.
     * @return  MD5 hash in a string.
     */
    public static String getMD5(File file) {
        MessageDigest md = getMessageDigest();
        String md5 = "";

        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            md5 = org.apache.commons.codec.digest.DigestUtils.md5Hex(fileInputStream);
            fileInputStream.close();

        } catch (IOException e) {
            System.out.println("FileHandler.getMD5() failed. Likely due to file \"" + file + "\" not being found" );
        }

        return md5;
    }

    /**
     * Returns all files in the directory of where the program resides + \external\
     */
    public static ArrayList<SerialFileAttr> getAllLocalFileInfo() {
        return getAllLocalFileInfo("external" + File.separatorChar);
    }

    /**
     * Returns all files in the directory (includes subdirectories)
     * @param path  Relative / absolute path of the directory.
     */
	public static ArrayList<SerialFileAttr> getAllLocalFileInfo(String path) {
	    File directory = new File(path);
	    if (!directory.exists()) {
	        directory.mkdir();
        }

        ArrayList<SerialFileAttr> files = new ArrayList<>();

		// Walk the files in the directory of 'this.path' and add them to the set.
		try {
			Files.walkFileTree(Paths.get(path), new SimpleFileVisitor<Path>() {
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                    Path getPath = Paths.get(path);
                    String relPath = file.toAbsolutePath().toString().substring(getPath.toAbsolutePath().toString().length()+1);
                    files.add(new SerialFileAttr(path, relPath, attrs));
                    return FileVisitResult.CONTINUE;
                }
            });
		} catch (IOException e) {
			e.printStackTrace();
		}

		return files;
	}
}
