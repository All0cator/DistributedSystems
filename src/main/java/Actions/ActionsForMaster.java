package Actions;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

import Primitives.*;
import Primitives.Payloads.FilterMasterPayload;
import Primitives.Payloads.FilterWorkerPayload;
import Primitives.Payloads.FoodCategoriesPayload;
import Primitives.Payloads.GetTotalCountResponsePayload;
import Primitives.Payloads.HostDataPayload;
import Primitives.Payloads.HostDiscoveryRequestPayload;
import Primitives.Payloads.MapTotalCountPayload;
import Primitives.Payloads.RatePayload;
import Primitives.Payloads.RegistrationPayload;
import Primitives.Payloads.ResultPayload;
import Primitives.Payloads.StoresPayload;
import Primitives.Payloads.TotalCountArrivalPayload;
import Primitives.Payloads.WorkerRegistrationPayload;
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

                        HostData workerHostData = ((RegistrationPayload)message.payload).hostData;

                        int workerID = master.RegisterWorker(workerHostData);

                        Socket workerConnection = new Socket(workerHostData.GetHostIP(), workerHostData.GetPort());

                        ObjectOutputStream sOStream = new ObjectOutputStream(workerConnection.getOutputStream());
                        
                        Message registrationReply = new Message();
                        registrationReply.type = MessageType.REGISTER_NODE;
                        WorkerRegistrationPayload pWorker = new WorkerRegistrationPayload();
                        registrationReply.payload = pWorker;
                        pWorker.workerID = workerID;
                        pWorker.jsonStores = this.master.GetStoresFromMemory(workerID); 

                        sOStream.writeObject(registrationReply);
                        sOStream.flush();
                    }
                    else {
                        master.RegisterReducer(((RegistrationPayload)message.payload).hostData);
                    }
                    break;
            
                case MessageType.GET_TOTAL_COUNT:
                {
                    HostData replyHostData = ((HostDataPayload)message.payload).hostData;

                    int mapID = master.requestPool.GetID(new HostData(replyHostData.GetHostIP(), replyHostData.GetPort()));

                    ArrayList<HostData> workerHostDatas = master.GetWorkerHostDatas();

                    Message mapMessage = new Message();
                    mapMessage.type = MessageType.MAP_TOTAL_COUNT;
                    MapTotalCountPayload pTotalCount = new MapTotalCountPayload();
                    pTotalCount.mapID = mapID;
                    pTotalCount.numWorkers = workerHostDatas.size();
                    mapMessage.payload = pTotalCount;
                    
                    for(HostData hostData : workerHostDatas) {

                        Socket socket = new Socket(hostData.GetHostIP(), hostData.GetPort());

                        ObjectOutputStream workerOStream = new ObjectOutputStream(socket.getOutputStream());
                        
                        workerOStream.writeObject(mapMessage);
                        workerOStream.flush();
                    }
                }
                break;
                    
                case MessageType.TOTAL_COUNT_ARRIVAL:
                {
                    HostData responseHostData = master.requestPool.ReturnID(((TotalCountArrivalPayload)message.payload).mapID);

                    Message result = new Message();
                    result.type = MessageType.GET_TOTAL_COUNT_RESPONSE;

                    GetTotalCountResponsePayload pTotalCountResponse = new GetTotalCountResponsePayload();
                    pTotalCountResponse.totalCount = ((TotalCountArrivalPayload)message.payload).totalCount;

                    result.payload = pTotalCountResponse;

                    Socket responseSocket = new Socket(responseHostData.GetHostIP(), responseHostData.GetPort());
                    ObjectOutputStream oStreamResponse = new ObjectOutputStream(responseSocket.getOutputStream());

                    oStreamResponse.writeObject(result);
                    oStreamResponse.flush();
                } 
                break;
                case MessageType.TOTAL_STORES_ARRIVAL:
                {
                    HostData responseHostData = master.requestPool.ReturnID(((StoresPayload)message.payload).mapID);

                    Message result = new Message();
                    result.type = MessageType.FILTER;
                    StoresPayload pStores = new StoresPayload();
                    result.payload = (StoresPayload)message.payload;

                    Socket responseSocket = new Socket(responseHostData.GetHostIP(), responseHostData.GetPort());
                    ObjectOutputStream oStreamResponse = new ObjectOutputStream(responseSocket.getOutputStream());

                    oStreamResponse.writeObject(result);
                    oStreamResponse.flush();
                }
                break;
                case MessageType.HOST_DISCOVERY:
                    HostDiscoveryRequestPayload pHostDiscoveryRequestMaster = (HostDiscoveryRequestPayload)message.payload;
                    
                    Message reply = new Message();
                    reply.type = MessageType.HOST_DISCOVERY;
                    HostDataPayload pHostDataPayload = new HostDataPayload();
                    reply.payload = pHostDataPayload;
                    
                    Message getHostData = new Message();
                    getHostData.type = MessageType.HOST_DISCOVERY;

                    if(!pHostDiscoveryRequestMaster.isWorkerNode) {
                        pHostDataPayload.hostData = master.GetReducerHostData();
                    }
                    else{
                        ArrayList<HostData> workersHostDatas2 = master.GetWorkerHostDatas();
                        if(pHostDiscoveryRequestMaster.index >= 0 && pHostDiscoveryRequestMaster.index < workersHostDatas2.size()) {
                            pHostDataPayload.hostData = workersHostDatas2.get(pHostDiscoveryRequestMaster.index);
                        }
                        else {
                            pHostDataPayload.hostData = new HostData("", -1);
                        }
                    }

                    // reply to connected node
                    this.oStream.writeObject(reply);
                    this.oStream.flush();

                    break;
                case MessageType.REFRESH:
                {
                    Message response = new Message();
                    response.type = MessageType.REFRESH;
                    FoodCategoriesPayload p = new FoodCategoriesPayload();
                    response.payload = p;
                    p.foodCategories = new ArrayList<String>();

                    this.master.GetFoodCategories(p.foodCategories);

                    HostData customerHostData = ((HostDataPayload)message.payload).hostData;

                    Socket customerConnection = new Socket(customerHostData.GetHostIP(), customerHostData.GetPort());
                    ObjectOutputStream oSStream = new ObjectOutputStream(customerConnection.getOutputStream());

                    oSStream.writeObject(response);
                    oSStream.flush();
                }
                break;
                case MessageType.FILTER:
                {
                    // Create a new request and pass filters to workers
                    FilterMasterPayload pFilter = (FilterMasterPayload)message.payload;
                    
                    int mapID = this.master.requestPool.GetID(pFilter.customerHostData);

                    ArrayList<HostData> workerHostDatas = this.master.GetWorkerHostDatas();
                    
                    Message workerMessage = new Message();
                    workerMessage.type = MessageType.FILTER;
                    FilterWorkerPayload pFilterWorker = new FilterWorkerPayload();
                    workerMessage.payload = pFilterWorker;
                    
                    pFilterWorker.filter = pFilter.filter;
                    pFilterWorker.mapID = mapID;
                    pFilterWorker.numWorkers = workerHostDatas.size();
                    pFilterWorker.customerLatitude = pFilter.customerLatitude;
                    pFilterWorker.customerLongitude = pFilter.customerLongitude;

                    for(HostData hostData : workerHostDatas) {
                        Socket newConnection = new Socket(hostData.GetHostIP(), hostData.GetPort());

                        ObjectOutputStream oStream = new ObjectOutputStream(newConnection.getOutputStream());


                        oStream.writeObject(workerMessage);
                        oStream.flush();
                    }
                    
                }
                break;
                case MessageType.RATE:
                {
                    RatePayload pRate = (RatePayload)message.payload;

                    Message rateMessage = new Message();
                    rateMessage.type = MessageType.RATE;
                    RatePayload pRateWorker = new RatePayload();
                    rateMessage.payload = pRateWorker;

                    pRateWorker.customerHostData = pRate.customerHostData;
                    pRateWorker.noOfStars = pRate.noOfStars;
                    pRateWorker.storeName = pRate.storeName;

                    int workerID = this.master.StoreNameToWorkerID(pRate.storeName);
                
                    ArrayList<HostData> workerHostDatas = this.master.GetWorkerHostDatas();

                    HostData workerHostData = workerHostDatas.get(workerID);

                    Socket workerConnection = new Socket(workerHostData.GetHostIP(), workerHostData.GetPort());

                    ObjectOutputStream oSStream = new ObjectOutputStream(workerConnection.getOutputStream());

                    oSStream.writeObject(rateMessage);
                    oSStream.flush();

                }
                break;
                case MessageType.RESULT:
                {
                    ResultPayload pResult = (ResultPayload)message.payload;
                    
                    Message resultMessage = new Message();
                    resultMessage.type = MessageType.RESULT;
                    resultMessage.payload = message.payload;
                    
                    Socket userConnection = new Socket(pResult.userHostData.GetHostIP(), pResult.userHostData.GetPort());

                    ObjectOutputStream oSStream = new ObjectOutputStream(userConnection.getOutputStream());
                    oSStream.writeObject(resultMessage);
                    oSStream.flush();
                }
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
