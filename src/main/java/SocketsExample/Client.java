package main.java.SocketsExample;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class Client extends Thread {
    //int a;
    //int b;

    //public Client(int a, int b) {
        //this.a = a;
        //this.b = b;
    //}

    Test t;

    public Client(Test t) {
        this.t = t;
    }

    public static void main(String[] args) {
        //new Client(1, 2).start();
        //new Client(1, 4).start();
        //new Client(1, 9).start();
        //new Client(1, 13).start();

        new Client(new Test(0, false)).start();
    }

    @Override
    public void run() {
        try {

            Socket socket = new Socket("localhost", 8081);
            ObjectInputStream iStream = new ObjectInputStream(socket.getInputStream());
            ObjectOutputStream oStream = new ObjectOutputStream(socket.getOutputStream());

            //oStream.writeInt(a);
            //oStream.flush();
            //oStream.writeInt(b);
            //oStream.flush();

            oStream.writeObject(t);
            oStream.flush();

            Test t = (Test)iStream.readObject();

            System.out.println("Server: " + t.getA() + " " + t.isFlag());

            socket.close();

        } catch(IOException e){
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
