package Actions;

import java.io.IOException;
import java.net.Socket;

import Nodes.Reducer;
import Primitives.Message;
import Primitives.MessageType;
import Nodes.Node;

public class ActionsForReducer extends ActionsForNode {
    private Reducer reducer;

    public ActionsForReducer(Socket connectionSocket, Reducer reducer) {
        super(connectionSocket);
        this.reducer = reducer;
    }

    public ActionsForReducer() {
        
    }

    @Override
    public ActionsForNode Instantiate(Socket connectionSocket, Node node) {
        return new ActionsForReducer(connectionSocket, (Reducer)node);
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
