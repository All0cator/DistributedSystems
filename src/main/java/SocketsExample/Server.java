package SocketsExample;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(8081);

        while(true){
            Socket inSocket = serverSocket.accept();
            ActionsForClients a = new ActionsForClients(inSocket);
            a.start();
        }
        
    }
}
