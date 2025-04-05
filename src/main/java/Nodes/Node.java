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
import Primitives.Payloads.HostDiscoveryRequestMasterPayload;
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

    /*protected HostData QueryHostData(HostData masterHostData, HostDiscoveryRequestMasterPayload payload) {
        try {
            Socket masterConnection = new Socket(masterHostData.GetHostIP(), masterHostData.GetPort());

            ObjectOutputStream oStream = new ObjectOutputStream(masterConnection.getOutputStream()); 
            ObjectInputStream iStream = new ObjectInputStream(masterConnection.getInputStream());

            Message message = new Message();
            message.type = MessageType.HOST_DISCOVERY;
            message.payload = payload;

            // Send HostDiscoveryPayload
            oStream.writeObject(message);
            oStream.flush();
            // Receive HostDataPayload

            Message messageIn = (Message)iStream.readObject();
            HostDataPayload pHostData = (HostDataPayload)messageIn.payload;

            return pHostData.hostData;
        } catch (IOException e) {
            throw new RuntimeException();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException();
        }
    }*/

    private void TerminateServerConnection() {
        try {
            this.serverSocket.close();
        } catch (IOException e) {
            throw new RuntimeException();
        }
    }
}
