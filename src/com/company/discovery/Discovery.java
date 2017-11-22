package com.company.discovery;

import com.company.Client;

import java.io.IOException;
import java.net.*;
import java.util.HashSet;

/**
 * Discovery is a thread-based discovery client that searches for a
 */
public class Discovery extends Thread {
    
    // Change List to Set for better performance + we don't really care about the order of the elements.
    /** List of IPs that are on our local network with the given port open + listening */
    static private HashSet<String> ips = new HashSet<>();
    /** Port to check for listening ServerSockets. */
    private int port;
    /** Is this the initial run? */
    private boolean initialRun;

    /**
     * Default constructor - just sets the searching port to 27015
     */
    public Discovery() {
        this(27015);
    }

    /**
     * Port constructor - sets the searching port to the given argument.
     * @param port      port to try to connect to when a computer is found on the LAN
     */
    public Discovery(int port) {
        this.port = port;
        this.initialRun = true;
    }

    @SuppressWarnings("InfiniteLoopStatement")
    public void run() {
        while (true) {
            getAvailableNodes();

            // Make initial run false after the first iteration of launching discovery.
            // From now on, we only want to push our local files to remote PC's.
            initialRun = false;

            try {
                sleep(15000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }

    /**
     * Finds the available nodes on the local network that have ServerSockets
     * open on port this.port and adds them to the this.ips ArrayList
     */
    private void getAvailableNodes() {
        int timeout = 300;
        String ip = getLocalIp();
        String subnet = getSubnet();

        // Find connected devices to this LAN
        // x.x.x.1 (like 192.168.1.1) is the default gateway, so ignore.
        for (int i = 2; i < 255; ++i) {
            String possibleNode = subnet + "." + i;

            // We've found this computer, so just skip it...
            if (possibleNode.equals(ip))
                continue;

            // Is there a computer at the given IPv4 address?
            if (isAddressReachable(possibleNode, timeout)) {
                System.out.println(possibleNode);
                // is it listening on our port?
                if (isListeningOnPort(possibleNode, port, timeout)) {
                    ips.add(possibleNode);

                    // If we find a node that is running our program, might as well sync with it too.
                    // If initialRun is true, this will push local files to the remote computer and pull remote files to the local copy of the remote PC's directory.
                    // If initialRun is false, this will only push local files to the remote computer.
                    new Thread(new Client(possibleNode, 21, initialRun)).start();
                } // end nested if
            } else {
                // If the address isn't reachable but is contained in our list, then we should remove it
                // so we don't try to connect to it anymore.
                if (ips.contains(possibleNode))
                    ips.remove(possibleNode);
            }
        }
    }

    /**
     * Checks to see if the given IPv4 address is reachable on our network
     * @param possibleNode IPv4 address of a possible node
     * @param timeout      ttl of the request
     * @return      TRUE: if possibleNode is reachable
     *              FALSE: if possibleNode is not reachable, or is empty string.
     */
    private boolean isAddressReachable(String possibleNode, int timeout) {
        if (possibleNode.isEmpty() || timeout == 0)
            return false;

        try {
            return InetAddress.getByName(possibleNode).isReachable(timeout);
        } catch (IOException e) {
            System.out.println("isAddressReachable() could not find host.");
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Checks to see if the given possibleNode IPv4 address is listening on the given port
     *
     * @param possibleNode  IPv4 address to test
     * @param port          port to try and check
     * @param timeout       ttl for request
     * @return      TRUE: on successful connection to the possibleNode @ port
     *              FALSE: on a non-successful connection to the possibleNode @ port
     *                     if possibleNode is empty string, or port is 0
     */
    private boolean isListeningOnPort(String possibleNode, int port, int timeout) {
        if (possibleNode.isEmpty() || port == 0 || timeout == 0)
            return false;

        Socket socket = null;
        try {
            socket = new Socket();
            socket.connect(new InetSocketAddress(possibleNode, port), timeout);

            return socket.isConnected();
        } catch (IOException e) {
            //System.out.println("isListeningOnPort() could not find host.");
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    //e.printStackTrace();
                }
            }
        }

        return false;
    }

    /**
     * Returns the local host IP of this computer (e.g. 192.168.1.2 or 127.0.0.1)
     *
     * @return IPv4 address of this computer
     */
    private String getLocalIp() {
        String ip = "";
        try {
            InetAddress localHost = Inet4Address.getLocalHost();
            ip = localHost.getHostAddress();
        } catch (UnknownHostException e) {
            System.out.println("getLocalIp() failed with unknown host.");
            e.printStackTrace();
        }

        return ip;
    }

    /**
     * Returns the subnet of the LAn. (e.g. 192.168.1 or 127.0.0);
     *
     * @return IPv4 subnet of this computer
     */
    private String getSubnet() {
        String ip = getLocalIp();

        return ip.substring(0, ip.lastIndexOf("."));
    }

    public HashSet<String> getIps() { return ips; }

    public boolean isReady() {
        return initialRun;
    }
}
