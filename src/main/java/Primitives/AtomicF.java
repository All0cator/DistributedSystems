package Primitives;

public class AtomicF {
    private float value;

    public AtomicF() {
        this.value = 0.0f;
    }

    public synchronized void Add(float value) {
        this.value += value;
    }

    public synchronized float GetValue() {
        return value;
    }

    public synchronized void SetValue(float value) {
        this.value = value;
    }
}
