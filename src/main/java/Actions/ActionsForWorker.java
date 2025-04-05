package Actions;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import Nodes.Node;
import Nodes.Worker;
import Primitives.HostData;
import Primitives.Message;
import Primitives.MessageType;
import Primitives.Payloads.HostDataPayload;
import Primitives.Payloads.HostDiscoveryRequestPayload;
import Primitives.Payloads.MapTotalCountPayload;
import Primitives.Payloads.ReduceTotalCountPayload;
import Primitives.Payloads.WorkerRegistrationPayload;

public class ActionsForWorker extends ActionsForNode {
    private Worker worker;

    public ActionsForWorker(Socket connectionSocket, Worker worker) {
        super(connectionSocket);
        this.worker = worker;
    }

    public ActionsForWorker() {
        
    }

    @Override
    public ActionsForNode Instantiate(Socket connectionSocket, Node node) {
        return new ActionsForWorker(connectionSocket, (Worker)node);
    }

    @Override
    public void run() {
        try {
            Message message = (Message)iStream.readObject();

            switch (message.type) {
                case REGISTER_NODE:
                    worker.SetID(((WorkerRegistrationPayload)message.payload).workerID);
                    break;
                case MAP_TOTAL_COUNT:

                    if(worker.GetReducerHostData().GetPort() < 0) {
                        // Host discovery
                        Message hostDiscoveryMessage = new Message();
                        hostDiscoveryMessage.type = MessageType.HOST_DISCOVERY;
                        HostDiscoveryRequestPayload pDiscovery = new HostDiscoveryRequestPayload();
                        hostDiscoveryMessage.payload = pDiscovery;
                        pDiscovery.isWorkerNode = false;

                        HostData masterHostData = worker.GetMasterHostData();

                        Socket masterConnection = new Socket(masterHostData.GetHostIP(), masterHostData.GetPort());

                        ObjectOutputStream sOStream = new ObjectOutputStream(masterConnection.getOutputStream());
                        ObjectInputStream sIStream = new ObjectInputStream(masterConnection.getInputStream());

                        sOStream.writeObject(hostDiscoveryMessage);
                        sOStream.flush();

                        Message reply = (Message)sIStream.readObject();
                        HostDataPayload pHostData = (HostDataPayload)reply.payload;

                        worker.SetReducerHostData(pHostData.hostData);
                    }

                    // Perform mapping
                    float data[] = Worker.workersData[worker.GetID()];
                    float totalCount[] = new float[1];
                    for(int i = 0; i < data.length; ++i) {
                        totalCount[0] += data[i];
                    }

                    // Assume we found Reducer

                    HostData reducerHostData = worker.GetReducerHostData();
                    Socket reducerConnection = new Socket(reducerHostData.GetHostIP(), reducerHostData.GetPort());

                    ObjectOutputStream sOStream = new ObjectOutputStream(reducerConnection.getOutputStream());

                    Message reduceMessage = new Message();
                    reduceMessage.type = MessageType.REDUCE_TOTAL_COUNT;
                    ReduceTotalCountPayload pReduceTotalCount = new ReduceTotalCountPayload();
                    reduceMessage.payload = pReduceTotalCount;
                    pReduceTotalCount.mapID = ((MapTotalCountPayload)message.payload).mapID;
                    pReduceTotalCount.numWorkers = ((MapTotalCountPayload)message.payload).numWorkers;
                    pReduceTotalCount.inCounters = totalCount;

                    sOStream.writeObject(reduceMessage);
                    sOStream.flush();

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
