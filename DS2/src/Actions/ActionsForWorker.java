package Actions;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectOutputStream.PutField;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import org.json.JSONObject;

import Nodes.Node;
import Nodes.Worker;
import Primitives.HostData;
import Primitives.Message;
import Primitives.MessageType;
import Primitives.Store;
import Primitives.Payloads.AddStorePayload;
import Primitives.Payloads.FilterWorkerPayload;
import Primitives.Payloads.FoodCategoriesPayload;
import Primitives.Payloads.HostDataPayload;
import Primitives.Payloads.HostDiscoveryRequestPayload;
import Primitives.Payloads.JSONStoresPayload;
import Primitives.Payloads.ManagerStatePayload;
import Primitives.Payloads.MapTotalCountPayload;
import Primitives.Payloads.PurchasePayload;
import Primitives.Payloads.RatePayload;
import Primitives.Payloads.ReduceTotalCountPayload;
import Primitives.Payloads.ReduceTotalRevenuePayload;
import Primitives.Payloads.RequestDataPayload;
import Primitives.Payloads.ResultPayload;
import Primitives.Payloads.StoresPayload;
import Primitives.Payloads.TotalRevenueRequestPayload;
import Primitives.Payloads.WorkerRegistrationPayload;

import static Primitives.MessageType.*;

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

    private void FindReducerHostData() throws UnknownHostException, IOException, ClassNotFoundException {
        if(worker.GetReducerHostData().GetPort() < 0) {
            // Host discovery
            Message hostDiscoveryMessage = new Message();
            hostDiscoveryMessage.type = HOST_DISCOVERY;
            HostDiscoveryRequestPayload pDiscovery = new HostDiscoveryRequestPayload();
            hostDiscoveryMessage.payload = pDiscovery;
            pDiscovery.isWorkerNode = false;

            HostData masterHostData = worker.GetMasterHostData();

            // Need input stream also cannot use SendMessageToNode
            Socket masterConnection = new Socket(masterHostData.GetHostIP(), masterHostData.GetPort());

            ObjectOutputStream sOStream = new ObjectOutputStream(masterConnection.getOutputStream());
            ObjectInputStream sIStream = new ObjectInputStream(masterConnection.getInputStream());

            sOStream.writeObject(hostDiscoveryMessage);
            sOStream.flush();

            Message reply = (Message)sIStream.readObject();
            HostDataPayload pHostData = (HostDataPayload)reply.payload;

            worker.SetReducerHostData(pHostData.hostData);
        }
    }

    @Override
    public void run() {
        try {
            Message message = (Message)iStream.readObject();

            switch (message.type) {
                case REGISTER_NODE:
                {
                    worker.SetID(((WorkerRegistrationPayload)message.payload).workerID);

                    
                    // Get JSON Stores from master and add them to worker
                    String stores[] = ((WorkerRegistrationPayload)message.payload).jsonStores;

                    if(stores != null) {
                        for(int i = 0; i < stores.length; ++i) {
                            this.worker.AddStore(new Store(new JSONObject(stores[i])));
                        }
                    }

                    this.worker.DebugStores();
                }
                break;
                case MAP_TOTAL_COUNT:
                {
                    FindReducerHostData();

                    // Perform mapping
                    float data[] = Worker.workersData[worker.GetID()];
                    float totalCount[] = new float[1];
                    for(int i = 0; i < data.length; ++i) {
                        totalCount[0] += data[i];
                    }

                    // Assume we found Reducer

                    Message reduceMessage = new Message();
                    reduceMessage.type = REDUCE_TOTAL_COUNT;
                    ReduceTotalCountPayload pReduceTotalCount = new ReduceTotalCountPayload();
                    reduceMessage.payload = pReduceTotalCount;
                    pReduceTotalCount.mapID = ((MapTotalCountPayload)message.payload).mapID;
                    pReduceTotalCount.numWorkers = ((MapTotalCountPayload)message.payload).numWorkers;
                    pReduceTotalCount.inCounters = totalCount;

                    SendMessageToNode(this.worker.GetReducerHostData(), reduceMessage);
                }
                break;
                case FILTER:
                {
                    FindReducerHostData();

                    FilterWorkerPayload pFilter = (FilterWorkerPayload)message.payload;

                    ArrayList<Store> stores = new ArrayList<Store>();
                    ArrayList<Store> workerStores = this.worker.GetStores();

                    // Actual filter code
                    for(int i = 0; i < workerStores.size(); ++i) {
                        Store store = workerStores.get(i);

                        double distanceKm = store.GetDistanceKm(pFilter.customerLatitude, pFilter.customerLongitude);

                        boolean distanceConstraint = distanceKm <= 5.0d;
                        boolean foodCategoryConstraint = pFilter.filter.foodCategories.contains(store.GetFoodCategory()) 
                        || pFilter.filter.foodCategories.size() == 0;
                        boolean starsConstraint = pFilter.filter.noOfStars.contains((int)store.GetStars())
                        || pFilter.filter.noOfStars.size() == 0;
                        boolean priceCategoryConstraint = pFilter.filter.priceCategories.contains(store.GetPriceCategory())
                        || pFilter.filter.priceCategories.size() == 0;

                        if(distanceConstraint && foodCategoryConstraint && starsConstraint && priceCategoryConstraint) {
                            stores.add(new Store(store)); // make deep copy
                        }
                    }

                    Message reduceMessage = new Message();
                    reduceMessage.type = FILTER;
                    StoresPayload pStores = new StoresPayload();
                    reduceMessage.payload = pStores;
                    pStores.stores = stores;
                    pStores.mapID = pFilter.mapID;
                    pStores.numWorkers = pFilter.numWorkers;

                    SendMessageToNode(this.worker.GetReducerHostData(), reduceMessage);
                }
                break;
                case RATE:
                {
                    RatePayload pRate = (RatePayload)message.payload;
                    boolean successful = this.worker.RateStore(pRate.storeName, pRate.noOfStars);

                    String result = successful ? String.format("Store %s has been rated with: %d Successfully!", pRate.storeName, pRate.noOfStars) :
                    "Failed to Rate Store with name: " + pRate.storeName;
                    
                    Message masterResponse = new Message();
                    masterResponse.type = RESULT;
                    ResultPayload pRes = new ResultPayload();
                    masterResponse.payload = pRes;

                    pRes.userHostData = pRate.customerHostData;
                    pRes.result = result;

                    HostData masterHostData = this.worker.GetMasterHostData();

                    SendMessageToNode(masterHostData, masterResponse);
                }
                break;
                case PURCHASE:
                {
                    PurchasePayload pPurchase = (PurchasePayload)message.payload;
                    boolean success = this.worker.PurchaseFromStore(pPurchase.purchase);

                    String result = success ? "Succesfully made Purchase: " + pPurchase.purchase : "Failed to make Purchase: " + pPurchase.purchase;

                    Message masterResponse = new Message();
                    masterResponse.type = RESULT;
                    ResultPayload pResult = new ResultPayload();
                    masterResponse.payload = pResult;

                    pResult.userHostData = pPurchase.userHostData;
                    pResult.result = result;

                    HostData masterHostData = this.worker.GetMasterHostData();

                    SendMessageToNode(masterHostData, masterResponse);
                }
                break;
                case REFRESH_MANAGER:
                {
                    FindReducerHostData();

                    RequestDataPayload pRequestData = (RequestDataPayload)message.payload;

                    Message reduceMessage = new Message();
                    reduceMessage.type = REDUCE_MANAGER_STATE;
                    ManagerStatePayload pState = new ManagerStatePayload();
                    reduceMessage.payload = pState;

                    pState.foodCategories = new HashSet<String>();
                    pState.productTypes = new HashSet<String>();
                    pState.storeNames = new HashSet<String>();
                    pState.mapID = pRequestData.mapID;
                    pState.numWorkers = pRequestData.numWorkers;

                    this.worker.GetManagerState(pState.foodCategories, pState.productTypes, pState.storeNames);

                    this.SendMessageToNode(this.worker.GetReducerHostData(), reduceMessage);
                }
                break;
                case REFRESH_CUSTOMER:
                {
                    FindReducerHostData();

                    RequestDataPayload pRequestData = (RequestDataPayload)message.payload;

                    
                    
                    Message reducerMessage = new Message();
                    reducerMessage.type = REDUCE_FOOD_CATEGORIES;
                    FoodCategoriesPayload pFood = new FoodCategoriesPayload();
                    reducerMessage.payload = pFood;

                    pFood.foodCategories = new HashSet<String>();
                    pFood.mapID = pRequestData.mapID;
                    pFood.numWorkers = pRequestData.numWorkers;
                    
                    this.worker.GetFoodCategories(pFood.foodCategories);

                    this.SendMessageToNode(this.worker.GetReducerHostData(), reducerMessage);
                }
                break;
                case ADD_STORE:
                {
                    AddStorePayload pStore = (AddStorePayload)message.payload;

                    Message masterMessage = new Message();
                    masterMessage.type = ADD_STORE_ARRIVAL;
                    masterMessage.payload = null;

                    if(this.worker.AddStore(pStore.store)) {
                        masterMessage.payload = pStore;
                    }

                    this.SendMessageToNode(this.worker.GetMasterHostData(), masterMessage);
                }
                break;
                case TOTAL_REVENUE_PER_FOOD_CATEGORY:
                {
                    FindReducerHostData();

                    TotalRevenueRequestPayload pRequest = (TotalRevenueRequestPayload)message.payload;

                    HashMap<String, Float> storeNameToTotalRevenue = this.worker.GetTotalRevenuePerFoodCategory(pRequest.type);

                    Message reducerMessage = new Message();
                    reducerMessage.type = REDUCE_TOTAL_REVENUE;
                    ReduceTotalRevenuePayload pReduce = new ReduceTotalRevenuePayload();
                    reducerMessage.payload = pReduce;

                    pReduce.mapID = pRequest.mapID;
                    pReduce.numWorkers = pRequest.numWorkers;
                    pReduce.storeNameToTotalRevenue = storeNameToTotalRevenue;

                    this.SendMessageToNode(this.worker.GetReducerHostData(), reducerMessage);
                }
                break;
                case TOTAL_REVENUE_PER_PRODUCT_TYPE:
                {
                    FindReducerHostData();

                    TotalRevenueRequestPayload pRequest = (TotalRevenueRequestPayload)message.payload;

                    HashMap<String, Float> storeNameToTotalRevenue = this.worker.GetTotalRevenuePerProductType(pRequest.type);

                    Message reducerMessage = new Message();
                    reducerMessage.type = REDUCE_TOTAL_REVENUE;
                    ReduceTotalRevenuePayload pReduce = new ReduceTotalRevenuePayload();
                    reducerMessage.payload = pReduce;

                    pReduce.mapID = pRequest.mapID;
                    pReduce.numWorkers = pRequest.numWorkers;
                    pReduce.storeNameToTotalRevenue = storeNameToTotalRevenue;

                    this.SendMessageToNode(this.worker.GetReducerHostData(), reducerMessage);
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
