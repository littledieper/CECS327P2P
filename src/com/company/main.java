package com.company;

import com.company.discovery.Discovery;
import com.company.discovery.DiscoveryServer;

public class main {
    public static void main(String[] args) {
        System.out.println("Starting Discovery");
        Discovery d = new Discovery();
        d.start();

        System.out.println("Starting Server");
        new Thread(new Server(21, 0)).start();

        // Wait while discover is still running initial discovery run, so that we don't have anyone attempt
        // to connect to us while that's happening.
        while(d.isReady()){}
        System.out.println("Discovery Finished ");

        System.out.println("Starting DiscoveryServer");
        DiscoveryServer ds = new DiscoveryServer();
        ds.start();
    }
}
