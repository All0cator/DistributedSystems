package com.example.efood_customer.Actions;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import com.example.efood_customer.Nodes.Node;
import com.example.efood_customer.Primitives.HostData;
import com.example.efood_customer.Primitives.Message;
import com.example.efood_customer.Primitives.MessageType;
import com.example.efood_customer.Primitives.Payloads.HostDataPayload;
import com.example.efood_customer.Primitives.Payloads.HostDiscoveryRequestPayload;
import com.example.efood_customer.Primitives.Payloads.RegistrationPayload;

import static com.example.efood_customer.Primitives.MessageType.GET_TOTAL_COUNT;

public abstract class ActionsForNode implements Runnable {

    protected Socket connectionSocket;
    protected ObjectInputStream iStream;
    protected ObjectOutputStream oStream;
    // implement this for each ActionsForNode type object
    public abstract ActionsForNode Instantiate(Socket connectionSocket, Node node);

    public ActionsForNode(Socket connectionSocket) {
        this.connectionSocket = connectionSocket;
        try {
            this.iStream = new ObjectInputStream(this.connectionSocket.getInputStream());
            this.oStream = new ObjectOutputStream(this.connectionSocket.getOutputStream());
        } catch (IOException e) {
            throw new RuntimeException();
        }
    }

    public ActionsForNode() {
        
    }

    public void GetTotalCount(HostData masterHostData, HostData nodeHostData) {
        try {
            Message message = new Message();
            message.type = GET_TOTAL_COUNT;
            HostDataPayload pHostData = new HostDataPayload();
            message.payload = pHostData;
            pHostData.hostData = nodeHostData;

            this.SendMessageToNode(masterHostData, message);
        } catch (IOException e) {
            throw new RuntimeException();
        } 
    }

    public void SendMessageToNode(HostData nodeHostData, Message message) throws UnknownHostException, IOException {
        Socket nodeConnection = new Socket(nodeHostData.GetHostIP(), nodeHostData.GetPort());

        ObjectOutputStream oStream = new ObjectOutputStream(nodeConnection.getOutputStream());

        oStream.writeObject(message);
        oStream.flush();
    }
}
