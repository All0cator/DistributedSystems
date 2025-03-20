import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ActionsForClients extends Thread {

    Socket socket;

    ObjectInputStream iStream;
    ObjectOutputStream oStream;

    public ActionsForClients(Socket socket) throws IOException {
        this.socket = socket;

        oStream = new ObjectOutputStream(socket.getOutputStream());
        iStream = new ObjectInputStream(socket.getInputStream());
    }

    @Override
    public void run(){
        try {
            //int a = iStream.readInt();
            //int b = iStream.readInt();

            //int s = a + b;

            //oStream.writeInt(s);

            Test t = (Test)iStream.readObject();
            t.setA(t.getA() + 1);
            t.setFlag(true);

            oStream.writeObject(t);
            oStream.flush();

        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch(ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}