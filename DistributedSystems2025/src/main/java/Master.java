import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import Actions.ActionsForCustomers;

public class Master extends Thread{

    public static void main(String[] args) throws IOException {

        ServerSocket serverSocket = new ServerSocket(4444);


        while (true){
            Socket socket = serverSocket.accept();
            ActionsForCustomers actionsForClients = new ActionsForCustomers(socket);
            actionsForClients.start();
        }

    }
}
