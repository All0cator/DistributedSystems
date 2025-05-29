package Actions;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.sql.PseudoColumnUsage;
import java.time.Period;
import java.util.ArrayList;
import java.util.HashSet;

import Primitives.*;
import Primitives.Payloads.AddStorePayload;
import Primitives.Payloads.EditStorePayload;
import Primitives.Payloads.FilterMasterPayload;
import Primitives.Payloads.FilterWorkerPayload;
import Primitives.Payloads.FoodCategoriesPayload;
import Primitives.Payloads.GetTotalCountResponsePayload;
import Primitives.Payloads.HostDataPayload;
import Primitives.Payloads.HostDiscoveryRequestPayload;
import Primitives.Payloads.ManagerStatePayload;
import Primitives.Payloads.MapTotalCountPayload;
import Primitives.Payloads.PurchasePayload;
import Primitives.Payloads.RatePayload;
import Primitives.Payloads.RegistrationPayload;
import Primitives.Payloads.RequestDataPayload;
import Primitives.Payloads.ResultPayload;
import Primitives.Payloads.StoresPayload;
import Primitives.Payloads.TotalCountArrivalPayload;
import Primitives.Payloads.TotalRevenueArrivalPayload;
import Primitives.Payloads.TotalRevenuePayload;
import Primitives.Payloads.TotalRevenueRequestPayload;
import Primitives.Payloads.WorkerRegistrationPayload;
import Nodes.Master;
import Nodes.Node;

import static Primitives.MessageType.*;

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
                case REGISTER_NODE:

                    if(((RegistrationPayload)message.payload).isWorkerNode) {

                        HostData workerHostData = ((RegistrationPayload)message.payload).hostData;

                        int workerID = master.RegisterWorker(workerHostData);
                        
                        Message registrationReply = new Message();
                        registrationReply.type = REGISTER_NODE;
                        WorkerRegistrationPayload pWorker = new WorkerRegistrationPayload();
                        registrationReply.payload = pWorker;
                        pWorker.workerID = workerID;
                        pWorker.jsonStores = this.master.GetStoresFromMemory(workerID); 
                        
                        this.SendMessageToNode(workerHostData, registrationReply);
                    }
                    else {
                        master.RegisterReducer(((RegistrationPayload)message.payload).hostData);
                    }
                    break;
            
                case GET_TOTAL_COUNT:
                {
                    HostData replyHostData = ((HostDataPayload)message.payload).hostData;

                    int mapID = master.requestPool.GetID(new HostData(replyHostData.GetHostIP(), replyHostData.GetPort()));

                    ArrayList<HostData> workerHostDatas = master.GetWorkerHostDatas();

                    Message mapMessage = new Message();
                    mapMessage.type = MAP_TOTAL_COUNT;
                    MapTotalCountPayload pTotalCount = new MapTotalCountPayload();
                    pTotalCount.mapID = mapID;
                    pTotalCount.numWorkers = workerHostDatas.size();
                    mapMessage.payload = pTotalCount;
                    
                    for(HostData hostData : workerHostDatas) {

                        this.SendMessageToNode(hostData, mapMessage);
                    }
                }
                break;
                    
                case TOTAL_COUNT_ARRIVAL:
                {
                    HostData responseHostData = master.requestPool.ReturnID(((TotalCountArrivalPayload)message.payload).mapID);

                    Message result = new Message();
                    result.type = GET_TOTAL_COUNT_RESPONSE;

                    GetTotalCountResponsePayload pTotalCountResponse = new GetTotalCountResponsePayload();
                    pTotalCountResponse.totalCount = ((TotalCountArrivalPayload)message.payload).totalCount;

                    result.payload = pTotalCountResponse;

                    this.SendMessageToNode(responseHostData, result);
                } 
                break;
                case TOTAL_REVENUE_ARRIVAL:
                {
                    TotalRevenueArrivalPayload pRevenue = (TotalRevenueArrivalPayload)message.payload;

                    HostData userHostData = this.master.requestPool.ReturnID(pRevenue.mapID);

                    Message userMessage = new Message();
                    userMessage.type = TOTAL_REVENUE_ARRIVAL;
                    userMessage.payload = pRevenue;

                    this.SendMessageToNode(userHostData, userMessage);
                    
                }
                break;
                case MANAGER_STATE_ARRIVAL:
                {
                    ManagerStatePayload pState = (ManagerStatePayload)message.payload;

                    HostData responseHostData = master.requestPool.ReturnID(pState.mapID);

                    Message result = new Message();
                    result.type = REFRESH_MANAGER;
                    result.payload = pState;

                    this.SendMessageToNode(responseHostData, result);
                }
                break;
                case TOTAL_STORES_ARRIVAL:
                {
                    HostData responseHostData = master.requestPool.ReturnID(((StoresPayload)message.payload).mapID);

                    Message result = new Message();
                    result.type = FILTER;
                    result.payload = (StoresPayload)message.payload;

                    this.SendMessageToNode(responseHostData, result);
                }
                break;
                case HOST_DISCOVERY:
                    HostDiscoveryRequestPayload pHostDiscoveryRequestMaster = (HostDiscoveryRequestPayload)message.payload;
                    
                    Message reply = new Message();
                    reply.type = HOST_DISCOVERY;
                    HostDataPayload pHostDataPayload = new HostDataPayload();
                    reply.payload = pHostDataPayload;
                    
                    Message getHostData = new Message();
                    getHostData.type = HOST_DISCOVERY;

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
                case REFRESH_CUSTOMER:
                {
                    HostDataPayload pHostData = (HostDataPayload)message.payload;

                    Message workerMessage = new Message();
                    workerMessage.type = REFRESH_CUSTOMER;
                    RequestDataPayload p = new RequestDataPayload();
                    workerMessage.payload = p;
                    
                    int mapID = this.master.requestPool.GetID(pHostData.hostData);
                    ArrayList<HostData> workerHostDatas = this.master.GetWorkerHostDatas();
                    
                    p.mapID = mapID;
                    p.numWorkers = workerHostDatas.size();

                    for(HostData hostData : workerHostDatas) {
                        this.SendMessageToNode(hostData, workerMessage);
                    }
                }
                break;
                case FOOD_CATEGORIES_ARRIVAL:
                {
                    FoodCategoriesPayload pFood = (FoodCategoriesPayload)message.payload;
                    
                    Message customerMessage = new Message();
                    customerMessage.type = REFRESH_CUSTOMER;
                    customerMessage.payload = pFood;

                    HostData userHostData = this.master.requestPool.ReturnID(pFood.mapID);

                    this.SendMessageToNode(userHostData, customerMessage);
                }
                break;
                case REFRESH_MANAGER:
                {
                    HostData replyHostData = ((HostDataPayload)message.payload).hostData;

                    int mapID = this.master.requestPool.GetID(replyHostData);

                    ArrayList<HostData> workerHostDatas = this.master.GetWorkerHostDatas();

                    Message workerMessage = new Message();
                    workerMessage.type = REFRESH_MANAGER;
                    RequestDataPayload pRequestData = new RequestDataPayload();
                    workerMessage.payload = pRequestData;
                    pRequestData.mapID = mapID;
                    pRequestData.numWorkers = workerHostDatas.size();

                    for(HostData hostData : workerHostDatas) {
                        this.SendMessageToNode(hostData, workerMessage);
                    }
                }
                break;
                case FILTER:
                {
                    // Create a new request and pass filters to workers
                    FilterMasterPayload pFilter = (FilterMasterPayload)message.payload;
                    
                    int mapID = this.master.requestPool.GetID(pFilter.customerHostData);

                    ArrayList<HostData> workerHostDatas = this.master.GetWorkerHostDatas();
                    
                    Message workerMessage = new Message();
                    workerMessage.type = FILTER;
                    FilterWorkerPayload pFilterWorker = new FilterWorkerPayload();
                    workerMessage.payload = pFilterWorker;
                    
                    pFilterWorker.filter = pFilter.filter;
                    pFilterWorker.mapID = mapID;
                    pFilterWorker.numWorkers = workerHostDatas.size();
                    pFilterWorker.customerLatitude = pFilter.customerLatitude;
                    pFilterWorker.customerLongitude = pFilter.customerLongitude;

                    for(HostData hostData : workerHostDatas) {
                        this.SendMessageToNode(hostData, workerMessage);
                    }
                    
                }
                break;
                case RATE:
                {
                    RatePayload pRate = (RatePayload)message.payload;

                    Message rateMessage = new Message();
                    rateMessage.type = RATE;
                    RatePayload pRateWorker = new RatePayload();
                    rateMessage.payload = pRateWorker;

                    pRateWorker.customerHostData = pRate.customerHostData;
                    pRateWorker.noOfStars = pRate.noOfStars;
                    pRateWorker.storeName = pRate.storeName;

                    int workerID = this.master.StoreNameToWorkerID(pRate.storeName);
                
                    ArrayList<HostData> workerHostDatas = this.master.GetWorkerHostDatas();

                    HostData workerHostData = workerHostDatas.get(workerID);

                    this.SendMessageToNode(workerHostData, rateMessage);
                }
                break;
                case RESULT:
                {
                    ResultPayload pResult = (ResultPayload)message.payload;
                    
                    Message resultMessage = new Message();
                    resultMessage.type = RESULT;
                    resultMessage.payload = message.payload;

                    this.SendMessageToNode(pResult.userHostData, resultMessage);
                }
                break;
                case PURCHASE:
                {
                    PurchasePayload pPurchase = (PurchasePayload)message.payload;

                    int workerID = this.master.StoreNameToWorkerID(pPurchase.purchase.storeName);

                    ArrayList<HostData> workerHostDatas = this.master.GetWorkerHostDatas();

                    HostData workerHostData = workerHostDatas.get(workerID);

                    Message workerMessage = new Message();
                    workerMessage.type = PURCHASE;
                    workerMessage.payload = pPurchase;

                    this.SendMessageToNode(workerHostData, workerMessage);
                }
                break;
                case ADD_STORE:
                {
                    AddStorePayload pStores = (AddStorePayload)message.payload;

                    ArrayList<HostData> workerHostDatas = this.master.GetWorkerHostDatas();

                    int workerID = this.master.StoreNameToWorkerID(pStores.store.GetName());

                    Message workerMessage = new Message();
                    workerMessage.type = ADD_STORE;
                    workerMessage.payload = pStores;

                    this.SendMessageToNode(workerHostDatas.get(workerID), workerMessage);
                }
                break;
                case ADD_STORE_ARRIVAL:
                {
                    AddStorePayload pStore = (AddStorePayload)message.payload;

                    Message userMessage = new Message();
                    userMessage.type = ADD_STORE;
                    ManagerStatePayload pState = new ManagerStatePayload();
                    userMessage.payload = pState;

                    pState.foodCategories = new HashSet<String>();
                    pState.productTypes = new HashSet<String>();
                    pState.stores = new ArrayList<Store>();

                    if(pStore != null) {
                        pState.foodCategories.add(pStore.store.GetFoodCategory());

                        ArrayList<Product> products = pStore.store.GetProducts(false);

                        for(Product p : products) {
                            pState.productTypes.add(p.GetType());
                        }

                        pState.stores.add(pStore.store);
                    }

                    this.SendMessageToNode(pStore.userHostData, userMessage);
                }
                break;
                case TOTAL_REVENUE_PER_FOOD_CATEGORY:
                {
                    TotalRevenuePayload pRevenue = (TotalRevenuePayload)message.payload;
                    
                    int mapID = this.master.requestPool.GetID(pRevenue.userHostData);
                    
                    ArrayList<HostData> workerHostDatas = this.master.GetWorkerHostDatas();
                    
                    Message workerMessage = new Message();
                    workerMessage.type = TOTAL_REVENUE_PER_FOOD_CATEGORY;
                    TotalRevenueRequestPayload pRequest = new TotalRevenueRequestPayload();
                    workerMessage.payload = pRequest;
                    
                    pRequest.mapID = mapID;
                    pRequest.numWorkers = workerHostDatas.size();
                    pRequest.type = pRevenue.type;
                    
                    for(HostData hostData : workerHostDatas) {
                        this.SendMessageToNode(hostData, workerMessage);
                    }
                }
                break;
                case TOTAL_REVENUE_PER_PRODUCT_TYPE:
                {
                    TotalRevenuePayload pRevenue = (TotalRevenuePayload)message.payload;
                    
                    int mapID = this.master.requestPool.GetID(pRevenue.userHostData);
                    
                    ArrayList<HostData> workerHostDatas = this.master.GetWorkerHostDatas();
                    
                    Message workerMessage = new Message();
                    workerMessage.type = TOTAL_REVENUE_PER_PRODUCT_TYPE;
                    TotalRevenueRequestPayload pRequest = new TotalRevenueRequestPayload();
                    workerMessage.payload = pRequest;
                    
                    pRequest.mapID = mapID;
                    pRequest.numWorkers = workerHostDatas.size();
                    pRequest.type = pRevenue.type;
                    
                    for(HostData hostData : workerHostDatas) {
                        this.SendMessageToNode(hostData, workerMessage);
                    }
                }
                break;
                case EDIT_STORE:
                {
                    EditStorePayload pEdit = (EditStorePayload)message.payload;

                    int ID = this.master.StoreNameToWorkerID(pEdit.storeName);
                
                    ArrayList<HostData> workerHostDatas = this.master.GetWorkerHostDatas();

                    Message workerMessage = new Message();
                    workerMessage.type = MessageType.EDIT_STORE;
                    workerMessage.payload = pEdit;

                    this.SendMessageToNode(workerHostDatas.get(ID), workerMessage);
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
