package Primitives;

public class RequestPool {
    private final HostData defaultHostData;
    private HostData pool[];
    private int size;
    private int maxSize;
    private int scanIndex;

    public RequestPool(int maxRequestCount) {
        this.defaultHostData = new HostData("", -1);
        this.pool = new HostData[maxRequestCount];
        this.size = 0;
        this.maxSize = maxRequestCount;
        this.scanIndex = 0;

        for(int i = 0; i < this.maxSize; ++i) {
            this.pool[i] = defaultHostData;
        }
    }

    // returns id of queued request
    public synchronized int GetID(HostData socketHostData) {

        while(this.size == this.maxSize) 
        {
            try {
                wait();
            } catch (InterruptedException e) {
            }
        }
        this.size++;

        while(this.pool[scanIndex].GetPort() >= 0) {scanIndex = (scanIndex + 1) % this.maxSize;}

        this.pool[scanIndex] = socketHostData;
        int result = scanIndex;
        scanIndex = (scanIndex + 1) % this.maxSize;

        return result;
    }

    public synchronized HostData ReturnID(int id) {
        if(id >= this.maxSize) { return new HostData(defaultHostData); }
        if(this.pool[id].GetPort() < 0) { return new HostData(defaultHostData); }

        HostData socketHostData = new HostData(this.pool[id]);

        this.pool[id] = defaultHostData;
        this.size--;

        notifyAll();

        return socketHostData;
    }
}
