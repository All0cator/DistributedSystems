package Actions;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import Nodes.Reducer;
import Primitives.HostData;
import Primitives.Message;
import Primitives.MessageType;
import Primitives.ReductionCompletionData;
import Primitives.Store;
import Primitives.Payloads.FoodCategoriesPayload;
import Primitives.Payloads.ManagerStatePayload;
import Primitives.Payloads.ReduceTotalCountPayload;
import Primitives.Payloads.ReduceTotalRevenuePayload;
import Primitives.Payloads.StoresPayload;
import Primitives.Payloads.TotalCountArrivalPayload;
import Primitives.Payloads.TotalRevenueArrivalPayload;
import Nodes.Node;

public class ActionsForReducer extends ActionsForNode {
    private Reducer reducer;

    public ActionsForReducer(Socket connectionSocket, Reducer reducer) {
        super(connectionSocket);
        this.reducer = reducer;
    }

    public ActionsForReducer() {
        
    }

    @Override
    public ActionsForNode Instantiate(Socket connectionSocket, Node node) {
        return new ActionsForReducer(connectionSocket, (Reducer)node);
    }

    @Override
    public void run() {
        try {
            Message message = (Message)iStream.readObject();

            switch (message.type) {
                case REDUCE_TOTAL_COUNT:
                {
                    ReduceTotalCountPayload pReduceTotalCount = (ReduceTotalCountPayload)message.payload;
                    reducer.Reduce(pReduceTotalCount.mapID, pReduceTotalCount.numWorkers, pReduceTotalCount.inCounters);

                    ReductionCompletionData data = reducer.ReductionCompletion(pReduceTotalCount.mapID);

                    if(data != null) {
                        Message totalCountArrivalMessage = new Message();
                        totalCountArrivalMessage.type = MessageType.TOTAL_COUNT_ARRIVAL;
                        TotalCountArrivalPayload pTotalCountArrival = new TotalCountArrivalPayload();
                        totalCountArrivalMessage.payload = pTotalCountArrival;
                        pTotalCountArrival.mapID = pReduceTotalCount.mapID;
                        pTotalCountArrival.totalCount = data.totalCount;

                        SendMessageToNode(this.reducer.GetMasterHostData(), totalCountArrivalMessage);
                    }
                }
                break;
                case MessageType.REDUCE_MANAGER_STATE:
                {
                    ManagerStatePayload pState = (ManagerStatePayload)message.payload;

                    reducer.ReduceManagerState(pState);

                    ManagerStatePayload data = reducer.ManagerStateReductionCompletion(pState.mapID);
                    
                    
                    if(data != null) {

                        Message managerStateArrivalMessage = new Message();
                        managerStateArrivalMessage.type = MessageType.MANAGER_STATE_ARRIVAL;
                        managerStateArrivalMessage.payload = data;

                        this.SendMessageToNode(this.reducer.GetMasterHostData(), managerStateArrivalMessage);
                    }
                }
                break;
                case REDUCE_FOOD_CATEGORIES:
                {
                    FoodCategoriesPayload pFood = (FoodCategoriesPayload)message.payload;

                    this.reducer.ReduceFoodCategories(pFood.mapID, pFood.numWorkers, pFood.foodCategories);

                    Set<String> foodCategories = this.reducer.FoodCategoriesReductionCompletion(pFood.mapID);

                    if(foodCategories != null) {
                        Message masterMessage = new Message();
                        masterMessage.type = MessageType.FOOD_CATEGORIES_ARRIVAL;
                        FoodCategoriesPayload pFood2 = new FoodCategoriesPayload();
                        masterMessage.payload = pFood2;
                        

                        pFood2.foodCategories = foodCategories;
                        pFood2.mapID = pFood.mapID;
                    
                        this.SendMessageToNode(this.reducer.GetMasterHostData(), masterMessage);
                    }
                }
                break;
                case MessageType.REDUCE_TOTAL_REVENUE:
                {
                    ReduceTotalRevenuePayload pReduce = (ReduceTotalRevenuePayload)message.payload;

                    this.reducer.ReduceRevenueByType(pReduce.mapID, pReduce.numWorkers, pReduce.storeNameToTotalRevenue);

                    HashMap<String, Float> data = this.reducer.RevenueByTypeReductionCompletion(pReduce.mapID);

                    if(data != null) {
                        Message masterMessage = new Message();
                        masterMessage.type = MessageType.TOTAL_REVENUE_ARRIVAL;
                        TotalRevenueArrivalPayload pRevenue = new TotalRevenueArrivalPayload();
                        masterMessage.payload = pRevenue;

                        pRevenue.mapID = pReduce.mapID;
                        pRevenue.storeNameToTotalRevenue = data;

                        this.SendMessageToNode(this.reducer.GetMasterHostData(), masterMessage);
                    }
                }
                break;
                case MessageType.FILTER:
                {
                    StoresPayload pStores = (StoresPayload)message.payload;

                    this.reducer.Reduce(pStores.mapID, pStores.numWorkers, pStores.stores);

                    ArrayList<Store> data = this.reducer.StoreReductionCompletion(pStores.mapID);
                    if(data != null) {
                        Message totalStoresArrivalMessage = new Message();
                        totalStoresArrivalMessage.type = MessageType.TOTAL_STORES_ARRIVAL;
                        StoresPayload ppStores = new StoresPayload();
                        totalStoresArrivalMessage.payload = ppStores;
                        ppStores.mapID = pStores.mapID;
                        ppStores.stores = data;
                        
                        SendMessageToNode(this.reducer.GetMasterHostData(), totalStoresArrivalMessage);
                    }
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
