package Actions;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ActionsForCustomers extends Thread{

    ObjectOutputStream objectOutputStream;
    ObjectInputStream objectInputStream;
    public ActionsForCustomers(Socket socket) throws IOException {
        objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
        objectInputStream = new ObjectInputStream(socket.getInputStream());
    }

    @Override
    public void run() {
        try {
            ServerSocket serverSocket = new ServerSocket(5009);
            Socket socket = new Socket("localhost", 5008);
            ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());

//            objectOutputStream.writeInt(a);
//            objectOutputStream.flush();
//            objectOutputStream.writeInt(b);
//            objectOutputStream.flush();
            // objectOutputStream.writeObject(test);
            // objectOutputStream.flush();
            // test = (Test) objectInputStream.readObject();
            // System.out.println("Test object: " + test);
//            System.out.println("The sum is: " + objectInputStream.readInt());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
