package pkgnew.books.checker;

public class Tuple2<T1, T2> {
     T1 a;
    T2 b;

    public Tuple2(T1 a, T2 b) {
        this.a = a;
        this.b = b;
    }

    public Tuple2() {
    }

    public T1 getA() {
        return a;
    }

    public void setA(T1 a) {
        this.a = a;
    }

    public T2 getB() {
        return b;
    }

    public void setB(T2 b) {
        this.b = b;
    }
    
    
}

