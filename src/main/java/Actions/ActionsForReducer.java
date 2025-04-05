package Actions;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;

import Nodes.Reducer;
import Primitives.HostData;
import Primitives.Message;
import Primitives.MessageType;
import Primitives.ReductionCompletionData;
import Primitives.Payloads.ReduceTotalCountPayload;
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

                    ReduceTotalCountPayload pReduceTotalCount = (ReduceTotalCountPayload)message.payload;
                    reducer.Reduce(pReduceTotalCount.mapID, pReduceTotalCount.numWorkers, pReduceTotalCount.inCounters);

                    ReductionCompletionData data = reducer.ReductionCompletion(pReduceTotalCount.mapID);

                    if(data != null) {
                        HostData masterHostData = reducer.GetMasterHostData();
    
                        Socket masterConnection = new Socket(masterHostData.GetHostIP(), masterHostData.GetPort());
    
                        ObjectOutputStream sOStream = new ObjectOutputStream(masterConnection.getOutputStream());
                        
                        Message totalCountArrivalMessage = new Message();
                        totalCountArrivalMessage.type = MessageType.TOTAL_COUNT_ARRIVAL;
                        TotalCountArrivalPayload pTotalCountArrival = new TotalCountArrivalPayload();
                        totalCountArrivalMessage.payload = pTotalCountArrival;
                        pTotalCountArrival.mapID = pReduceTotalCount.mapID;
                        pTotalCountArrival.totalCount = data.totalCount;
    
                        sOStream.writeObject(totalCountArrivalMessage);
                        sOStream.flush();
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
