package Actions;

import java.io.IOException;
import java.net.Socket;

import Nodes.CustomerApp;
import Nodes.Node;
import Primitives.Message;
import Primitives.MessageType;
import Primitives.Payloads.GetTotalCountResponsePayload;

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
                case MessageType.GET_TOTAL_COUNT_RESPONSE:
                    System.out.println("Total Count is: " + ((GetTotalCountResponsePayload)message.payload).totalCount);
                    break;
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
