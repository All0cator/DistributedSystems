package Primitives;

import java.util.ArrayList;
import java.util.HashMap;

public class AtomicMapStof {
    private HashMap<String, Float> values;

    public AtomicMapStof() {
        this.values = new HashMap<String, Float>();
    }

    public synchronized void Add(String type, float value) {
        if(this.values.get(type) == null) {
            this.values.put(type, 0.0f);
        }

        this.values.put(type, values.get(type) + value);
    }

    public synchronized HashMap<String, Float> GetValue() {
        HashMap<String, Float> result = new HashMap<String, Float>();

        result.putAll(this.values);

        return result;
    }

    public synchronized void SetValue(HashMap<String, Float> values) {
        this.values = values;
    }
}
