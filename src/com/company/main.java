package com.company;
import com.company.discovery.Discovery;
import com.company.discovery.DiscoveryServer;

import java.net.*;
import java.io.*;
import java.util.HashSet;

public class main {
    /*
     *  1) Find all IPS using Discovery
     *  2) Once Discovery has looked through the entire subnet, get the ip hashset
     *  3) Go through the hashset and connect to the each IP
     *      a) Connect the Socket
     *      b) do a pull of filehandler information
     *      c) create the subdirectories and files
     *      d) push the current node's subdirectory to the other nodes, most likely empty
     *  4) Run Server
     */
}
    public static void main(String[] args){
        Discovery d = new Discovery();
        DiscoveryServer ds = new DiscoveryServer();
        HashSet<String> ips;


        d.start();
        while(!d.initialPass()){//waiting for discovery to iterate through the subnet}
        for(String ip : d.getIps()){

        }



    }
}
