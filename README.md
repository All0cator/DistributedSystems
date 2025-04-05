# Delivery App - Distributed Systems



![alt text](src/main/resources/DeliveryImage.png)

## **Installation**

```
 git clone https://github.com/All0cator/DistributedSystems.git
```

## **Build**
### **Important before running anything**
```
./gradlew build
```

**IP Address Notation:**   
IPv4: 198.168.1.1  
IPv6: [23F3:1234:3238:DFE1:0063:0000:0000:FEFB]

## **Run Master Reducer Worker**
**Args:**  
HostIP for Master Node, Port Number
```
./gradlew runMaster --args="localhost 8080"
```
**Args:**   
HostIP for Node to be launched,  
Port Number for Node to be launched,  
HostIP for Master Node,  
Port Number of Master
```
./gradlew runReducer --args="localhost 8081 localhost 8080"
```
```
./gradlew runWorker --args="localhost 8082 localhost 8080"
```

## **Run Customer/Manager App**
```
./gradlew runCustomerApp --args="localhost 8083 localhost 8080"
```
```
./gradlew runManagerApp --args="localhost 8084 localhost 8080"
```

### Backend Architecture

![alt text](src/main/resources/BackendArchitecture.png)
(diagram from project description)
