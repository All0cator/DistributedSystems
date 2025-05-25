package Primitives;

import java.io.Serializable;

public class HostData implements Serializable {
    private String hostIP;
    private int port;

    public HostData(String hostIP, int port) {
        this.hostIP = hostIP;
        this.port = port;
    }

    public HostData(HostData other) {
        this.hostIP = other.GetHostIP();
        this.port = other.GetPort();
    }

    public synchronized String GetHostIP() {
        return this.hostIP;
    }

    public synchronized int GetPort() {
        return this.port;
    }

    public synchronized void SetHostIP(String hostIP) {
        this.hostIP = hostIP;
    }

    public synchronized void SetPort(int port) {
        this.port = port;
    }
}
