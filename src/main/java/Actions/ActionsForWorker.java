package Actions;

import java.io.IOException;
import java.net.Socket;

import Nodes.Node;
import Nodes.Worker;
import Primitives.Message;
import Primitives.MessageType;

public class ActionsForWorker extends ActionsForNode {
    private Worker worker;

    public ActionsForWorker(Socket connectionSocket, Worker worker) {
        super(connectionSocket);
        this.worker = worker;
    }

    public ActionsForWorker() {
        
    }

    @Override
    public ActionsForNode Instantiate(Socket connectionSocket, Node node) {
        return new ActionsForWorker(connectionSocket, (Worker)node);
    }

    @Override
    public void run() {
        try {
            Message message = (Message)iStream.readObject();

            switch (message.type) {
 
                default:
                    break;
            }

        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }   
    }
}
