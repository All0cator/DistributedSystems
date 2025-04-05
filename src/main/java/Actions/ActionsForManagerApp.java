package Actions;

import java.io.IOException;
import java.net.Socket;

import Nodes.ManagerApp;
import Nodes.Node;
import Primitives.Message;

public class ActionsForManagerApp extends ActionsForNode{
    private ManagerApp managerApp;

    public ActionsForManagerApp(Socket connectionSocket, ManagerApp managerApp) {
        super(connectionSocket);
        this.managerApp = managerApp;
    }

    public ActionsForManagerApp() {
        
    }

    @Override
    public ActionsForNode Instantiate(Socket connectionSocket, Node node) {
        return new ActionsForManagerApp(connectionSocket, (ManagerApp)node);
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
