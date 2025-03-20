import java.io.Serializable;

public class Test implements Serializable {
    int a;
    boolean flag;

    public Test(int a, boolean flag) {
        this.a = a;
        this.flag = flag;
    }

    public void setA(int a) {
        this.a = a;
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }

    public int getA() {
        return a;
    }

    public boolean isFlag() {
        return flag;
    }
}
