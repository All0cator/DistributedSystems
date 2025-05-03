package Actions;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

import Nodes.Reducer;
import Primitives.HostData;
import Primitives.Message;
import Primitives.MessageType;
import Primitives.ReductionCompletionData;
import Primitives.Store;
import Primitives.Payloads.ReduceTotalCountPayload;
import Primitives.Payloads.StoresPayload;
import Primitives.Payloads.TotalCountArrivalPayload;
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
