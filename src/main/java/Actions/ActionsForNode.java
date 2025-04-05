package Actions;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import Nodes.Node;
import Primitives.HostData;
import Primitives.Message;
import Primitives.MessageType;
import Primitives.Payloads.HostDataPayload;
import Primitives.Payloads.HostDiscoveryRequestPayload;
import Primitives.Payloads.RegistrationPayload;

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

            Socket nodeConnection = new Socket(masterHostData.GetHostIP(), masterHostData.GetPort());

            ObjectOutputStream oStream = new ObjectOutputStream(nodeConnection.getOutputStream()); 
            ObjectInputStream iStream = new ObjectInputStream(nodeConnection.getInputStream());

            Message message = new Message();
            message.type = MessageType.GET_TOTAL_COUNT;
            HostDataPayload pHostData = new HostDataPayload();
            message.payload = pHostData;
            pHostData.hostData = nodeHostData;

            oStream.writeObject(message);
            oStream.flush();
        } catch (IOException e) {
            throw new RuntimeException();
        } 
    }

    public void RegisterNodeToMaster(HostData masterHostData, RegistrationPayload payload) {
        try {
            Socket nodeConnection = new Socket(masterHostData.GetHostIP(), masterHostData.GetPort());

            ObjectOutputStream oStream = new ObjectOutputStream(nodeConnection.getOutputStream()); 
            ObjectInputStream iStream = new ObjectInputStream(nodeConnection.getInputStream());

            Message message = new Message();
            message.type = MessageType.REGISTER_NODE;
            message.payload = payload;

            oStream.writeObject(message);
            oStream.flush();
        } catch (IOException e) {
            throw new RuntimeException();
        }
    }
}
