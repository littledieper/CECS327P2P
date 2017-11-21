package com.company;

import java.net.*;
import java.io.*;

public class Server extends NetworkProtocol implements Runnable
{
    private ServerSocket serverSocket;
    private int port;
    private int timeout;

    public static void main(String args[]) {
        Server server = new Server(21, 0);
        new Thread(server).start();
    }

    public Server()
    {
        this(21, 0);
    }

    public Server(int port)
    {
        this(port, 0);
    }

    public Server(int port, int timeout)
    {
        this.port = port;
        this.timeout = timeout;
        makeServerSocket();
    }

    @SuppressWarnings("InfiniteLoopStatement")
    public void run() {
        while(true) {
            Socket socket = waitForClient();

            // Dispatch created socket to a worker server thread.
            if (socket != null) {
                Runnable serverTask = new ServerTask(socket, false);
                new Thread(serverTask).start();  // might want to create a thread pool here, we'll see.
            }
        }
    }

    /**
     * Waits for a connection to the serverSocket.
     * @return  Socket to remote computer the a serverSocket's accept.
     */
    private Socket waitForClient() {
        System.out.println("Waiting for server on port " + this.serverSocket.getLocalPort() + "...");

        try {
            return serverSocket.accept();
        } catch (SocketTimeoutException s) {
            System.out.println("Socket timed out!");
            s.printStackTrace();
        } catch (IOException e) {
            System.out.println("Failed to connect to port");
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Initialization of ServerSocket.
     */
    private void makeServerSocket() {
        try {
            this.serverSocket = new ServerSocket(this.port);
            this.serverSocket.setSoTimeout(this.timeout);
        } catch (IOException e) {
            System.out.println("Cannot make a server socket at port " + this.port);
            e.printStackTrace();
        }
    }
}