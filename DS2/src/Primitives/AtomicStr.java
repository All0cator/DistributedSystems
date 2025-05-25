package Primitives;

import java.util.ArrayList;
import java.util.HashSet;

public class AtomicStr {
    private HashSet<String> values;

    public AtomicStr() {
        this.values = new HashSet<String>();
    }

    public synchronized void Add(String value) {
        this.values.add(value);
    }

    public synchronized HashSet<String> GetValue() {
        HashSet<String> result = new HashSet<String>();

        result.addAll(this.values);

        return result;
    }

    public synchronized void SetValue(HashSet<String> values) {
        this.values = values;
    }
}
