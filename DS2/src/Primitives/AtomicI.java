package Primitives;

public class AtomicI {
    private int value;

    public AtomicI() {
        this.value = 0;
    }

    public synchronized void Add(int value) {
        this.value += value;
    }

    public synchronized int GetValue() {
        return value;
    }

    public synchronized void SetValue(int value) {
        this.value = value;
    }
}
