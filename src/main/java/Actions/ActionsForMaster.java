package Actions;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

import Primitives.*;
import Primitives.Payloads.GetTotalCountResponsePayload;
import Primitives.Payloads.HostDataPayload;
import Primitives.Payloads.HostDiscoveryRequestMasterPayload;
import Primitives.Payloads.HostDiscoveryRequestPayload;
import Primitives.Payloads.MapTotalCountPayload;
import Primitives.Payloads.RegistrationPayload;
import Primitives.Payloads.TotalCountArrivalPayload;
import Nodes.Master;
import Nodes.Node;

public class ActionsForMaster extends ActionsForNode {
    private Master master;

    public ActionsForMaster(Socket connectionSocket, Master master) {
        super(connectionSocket);
        this.master = master;
    }

    public ActionsForMaster() {
        
    }

    @Override
    public ActionsForNode Instantiate(Socket connectionSocket, Node node) {
        return new ActionsForMaster(connectionSocket, (Master)node);
    }

    @Override
    public void run() {
        try {
            Message message = (Message)iStream.readObject();

            switch (message.type) {
                case MessageType.REGISTER_NODE:

                    if(((RegistrationPayload)message.payload).isWorkerNode) {
                        master.RegisterWorker(((RegistrationPayload)message.payload).hostData);
                    }
                    else {
                        master.RegisterReducer(((RegistrationPayload)message.payload).hostData);
                    }
                    break;
            
                case MessageType.GET_TOTAL_COUNT:
                    HostDataPayload p = (HostDataPayload)message.payload;
                    int mapID = master.requestPool.GetID(new HostData(connectionSocket.getInetAddress().getHostAddress(), connectionSocket.getPort()));

                    ArrayList<HostData> workerHostDatas = master.GetWorkerHostDatas();

                    Message mapMessage = new Message();
                    mapMessage.type = MessageType.MAP_TOTAL_COUNT;
                    MapTotalCountPayload pTotalCount = new MapTotalCountPayload();
                    pTotalCount.mapID = mapID;
                    
                    for(HostData hostData : workerHostDatas) {

                        Socket socket = new Socket(hostData.GetHostIP(), hostData.GetPort());

                        ObjectOutputStream workerOStream = new ObjectOutputStream(socket.getOutputStream());
                        
                        workerOStream.writeObject(mapMessage);
                        workerOStream.flush();
                    }
                    break;
                    
                case MessageType.TOTAL_COUNT_ARRIVAL:
                    HostData responseHostData = master.requestPool.ReturnID(((TotalCountArrivalPayload)message.payload).mapID);
                    
                    Message result = new Message();
                    result.type = MessageType.GET_TOTAL_COUNT_RESPONSE;

                    GetTotalCountResponsePayload pTotalCountResponse = new GetTotalCountResponsePayload();
                    pTotalCountResponse.totalCount = ((TotalCountArrivalPayload)message.payload).totalCount;

                    result.payload = (Object)pTotalCountResponse;

                    Socket responseSocket = new Socket(responseHostData.GetHostIP(), responseHostData.GetPort());
                    ObjectOutputStream oStreamResponse = new ObjectOutputStream(responseSocket.getOutputStream());

                    oStreamResponse.writeObject(result);
                    oStreamResponse.flush();
                    break;
                    
                case MessageType.HOST_DISCOVERY:
                    HostDiscoveryRequestMasterPayload pHostDiscoveryRequestMaster = (HostDiscoveryRequestMasterPayload)message.payload;
                    
                    Message reply = new Message();
                    reply.type = MessageType.HOST_DISCOVERY;
                    HostDataPayload pHostDataPayload = new HostDataPayload();
                    reply.payload = pHostDataPayload;
                    
                    Message getHostData = new Message();
                    getHostData.type = MessageType.HOST_DISCOVERY;

                    if(pHostDiscoveryRequestMaster.isReducer) {
                        HostData reducerHostData = master.GetReducerHostData();

                        if(reducerHostData.GetPort() >= 0) {
                            Socket s = new Socket(reducerHostData.GetHostIP(), reducerHostData.GetPort());

                            ObjectOutputStream sOStream = new ObjectOutputStream(s.getOutputStream());
                            ObjectInputStream sIStream = new ObjectInputStream(s.getInputStream());

                            sOStream.writeObject(getHostData);
                            sOStream.flush();

                            Message hostData = (Message)sIStream.readObject();
                            pHostDataPayload.hostData = ((HostDataPayload)hostData.payload).hostData;

                            // TODO socket memory leak close socket after reading object using while loop
                        }
                        else {
                            pHostDataPayload.hostData = new HostData("", -1);
                        }
                    }
                    else{
                        ArrayList<HostData> workersHostDatas2 = master.GetWorkerHostDatas();
                        if(pHostDiscoveryRequestMaster.index > 0 && pHostDiscoveryRequestMaster.index < workersHostDatas2.size()) {
                            HostData workerHostData = workersHostDatas2.get(pHostDiscoveryRequestMaster.index);

                            Socket s = new Socket(workerHostData.GetHostIP(), workerHostData.GetPort());

                            ObjectOutputStream sOStream = new ObjectOutputStream(s.getOutputStream());
                            ObjectInputStream sIStream = new ObjectInputStream(s.getInputStream());

                            sOStream.writeObject(getHostData);
                            sOStream.flush();

                            Message hostData = (Message)sIStream.readObject();
                            pHostDataPayload.hostData = ((HostDataPayload)hostData.payload).hostData;
                        }
                        else {
                            pHostDataPayload.hostData = new HostData("", -1);
                        }
                    }

                    // reply to connected node
                    this.oStream.writeObject(reply);
                    this.oStream.flush();

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
