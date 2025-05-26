package com.example.efood_customer.Nodes;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import com.example.efood_customer.Actions.ActionsForNode;
import com.example.efood_customer.Primitives.HostData;
import com.example.efood_customer.Primitives.Message;
import com.example.efood_customer.Primitives.MessageType;
import com.example.efood_customer.Primitives.Payloads.HostDataPayload;
import com.example.efood_customer.Primitives.Payloads.HostDiscoveryRequestPayload;

public abstract class Node extends Thread {
    protected ActionsForNode actions;

    protected HostData hostData;
    protected ServerSocket serverSocket;

    public Node(String hostIP, int port) {

        this.hostData = new HostData(hostIP, port);
    }

    public String GetHostIP() {
        return this.hostData.GetHostIP();
    }

    public int GetPort() {
        return this.hostData.GetPort();
    }

    public void Start() {
        // override this method to connect to master from a Node
    }

    @Override
    public void run() {
        try {
            // TODO: Change server socket to bind to correct IP address
            this.serverSocket = new ServerSocket(this.hostData.GetPort());
        } catch (IOException e) {
            throw new RuntimeException();
        }

        Start();

        boolean isOpen = true;

        while(isOpen) {
            Socket clientSocket;
            try {
                clientSocket = serverSocket.accept();
                
                Runnable runnable = actions.Instantiate(clientSocket, this);
                Thread thread = new Thread(runnable);
                thread.start();

            } catch (IOException e) {
                TerminateServerConnection();
                throw new RuntimeException();
            }
        }

        TerminateServerConnection();
    }

    private void TerminateServerConnection() {
        try {
            this.serverSocket.close();
        } catch (IOException e) {
            throw new RuntimeException();
        }
    }
}
