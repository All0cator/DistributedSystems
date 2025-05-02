package Primitives;

import java.util.ArrayList;

public class AtomicS {
    private ArrayList<Store> values;

    public AtomicS() {
        this.values = new ArrayList<Store>();
    }

    public synchronized void Add(Store value) {
        this.values.add(value);
    }

    public synchronized ArrayList<Store> GetValue() {
        ArrayList<Store> result = new ArrayList<Store>();

        for(int i = 0; i < this.values.size(); ++i) {
            result.add(new Store(this.values.get(i)));
        }

        return result;
    }

    public synchronized void SetValue(ArrayList<Store> values) {
        this.values = values;
    }
}
