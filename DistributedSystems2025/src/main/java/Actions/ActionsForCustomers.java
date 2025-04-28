package Actions;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
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

//        try {
        //     ArrayList<Argument> argument = (ArrayList<Argument> ) objectInputStream.readObject();

        //    List<ServerArgument> ar = argument.stream().map(s ->{
        //        byte[] name = s.getName().replace("-","#").getBytes(StandardCharsets.UTF_8);
        //        ServerArgument t = new ServerArgument(name,s.getValue());
        //        return t;}).toList();

        //     for (ServerArgument serverArgument : ar) {
        //         System.out.println(serverArgument);
        //     }

        //     argument= argument.stream().filter(s->s.getValue()>50000)
        //             .filter(s->s.getValue()<70000)
        //             .collect(Collectors.toCollection(ArrayList::new));

        //     for(int i=0;i<argument.size();i++){
        //         System.out.println(argument.get(i).getName());
        //     }
        //     objectOutputStream.writeObject(argument);
        //     objectOutputStream.flush();

//        } catch (IOException | ClassNotFoundException e) {
//            throw new RuntimeException(e);
//        }
    }
}
