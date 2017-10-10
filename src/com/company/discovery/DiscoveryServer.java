package com.company.discovery;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * DiscoveryServer is a thread-based server that listens to a port for incoming ping connections. It has no
 * data processing whatsoever and just listens so that computers wanting to connect to this "node" on this port
 * can make a connection.
 */
public class DiscoveryServer extends Thread{

    /** DiscoveryServer should never timeout, i.e. it's 0 */
    private final int timeout = 0;
    /** ServerSocket for the DisoveryServer that listens to a port */
    private ServerSocket serverSocket;
    /** Port that the serverSocket will listen on */
    private int port;

    /**
     *  Default constructor - creatses a serverSocket that defaults to listening port 27015.
     */
    public DiscoveryServer() {
        this.port = 27015;
        makeServerSocket();
    }

    /**
     * Port constructor - creates a serverSocket that listens to the given port
     * @param port   port to listen on
     */
    public DiscoveryServer(int port) {
        this.port = port;
        makeServerSocket();
    }

    @SuppressWarnings("InfiniteLoopStatement")
    public void run() {
        while (true) {
            try {
                Socket socket = this.serverSocket.accept();
                if (socket == null)
                    continue;

                System.out.println("DiscoveryServer recieved connection from: " +
                                    socket.getRemoteSocketAddress());
            } catch (IOException e) {
                System.out.println("DiscoveryServer.run() accept failed.");
            }
        }
    }

    private void makeServerSocket() {
        try {
            this.serverSocket = new ServerSocket(this.port);
            this.serverSocket.setSoTimeout(this.timeout);
        } catch (IOException e) {
            System.out.println("DisoveryServer.makeServerSocket() failed to make a serverSocket.");
        }
    }
} // end DisvoeryServer.java
