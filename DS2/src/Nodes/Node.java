package Nodes;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import Actions.ActionsForNode;
import Primitives.HostData;
import Primitives.Message;
import Primitives.MessageType;
import Primitives.Payloads.HostDataPayload;
import Primitives.Payloads.HostDiscoveryRequestPayload;

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
