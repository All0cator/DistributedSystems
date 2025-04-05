package Actions;

import java.io.IOException;
import java.net.Socket;

import Nodes.CustomerApp;
import Nodes.Node;
import Primitives.Message;

public class ActionsForCustomerApp extends ActionsForNode {
    private CustomerApp customerApp;

    public ActionsForCustomerApp(Socket connectionSocket, CustomerApp customerApp) {
        super(connectionSocket);
        this.customerApp = customerApp;
    }

    public ActionsForCustomerApp() {
        
    }

    @Override
    public ActionsForNode Instantiate(Socket connectionSocket, Node node) {
        return new ActionsForCustomerApp(connectionSocket, (CustomerApp)node);
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
