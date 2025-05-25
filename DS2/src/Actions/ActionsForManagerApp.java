package Actions;

import java.io.IOException;
import java.net.Socket;
import java.util.Map;

import Nodes.ManagerApp;
import Nodes.Node;
import Primitives.Message;
import Primitives.MessageType;
import Primitives.Payloads.GetTotalCountResponsePayload;
import Primitives.Payloads.ManagerStatePayload;
import Primitives.Payloads.ResultPayload;
import Primitives.Payloads.TotalRevenueArrivalPayload;

public class ActionsForManagerApp extends ActionsForNode{
    private ManagerApp managerApp;

    public ActionsForManagerApp(Socket connectionSocket, ManagerApp managerApp) {
        super(connectionSocket);
        this.managerApp = managerApp;
    }

    public ActionsForManagerApp() {
        
    }

    @Override
    public ActionsForNode Instantiate(Socket connectionSocket, Node node) {
        return new ActionsForManagerApp(connectionSocket, (ManagerApp)node);
    }

    @Override
    public void run() {
        try {
            Message message = (Message)iStream.readObject();

            switch (message.type) {
                case GET_TOTAL_COUNT_RESPONSE:
                    System.out.println("Total Count is: " + ((GetTotalCountResponsePayload)message.payload).totalCount);
                    break;
                case RESULT:
                    System.out.println(((ResultPayload)message.payload).result);
                    break;
                case REFRESH_MANAGER:
                {
                    ManagerStatePayload pState = (ManagerStatePayload)message.payload;
                    
                    this.managerApp.UpdateState(pState.foodCategories, pState.productTypes, pState.stores);

                    this.managerApp.DebugState();
                }
                break;
                case ADD_STORE:
                {
                    ManagerStatePayload pState = (ManagerStatePayload)message.payload;

                    this.managerApp.UpdateStateIncremental(pState.foodCategories, pState.productTypes, pState.stores);

                    this.managerApp.DebugState();
                }
                break;
                case TOTAL_REVENUE_ARRIVAL:
                {
                    TotalRevenueArrivalPayload pRevenue = (TotalRevenueArrivalPayload)message.payload;

                    for(Map.Entry<String, Float> e : pRevenue.storeNameToTotalRevenue.entrySet()) {
                        System.out.printf("\"%s\": %.2f\n", e.getKey(), e.getValue());
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
