package snacks.lang;

public class Tuple3<T0, T1, T2> {

    private final T0 _0;
    private final T1 _1;
    private final T2 _2;

    public Tuple3(T0 _0, T1 _1, T2 _2) {
        this._0 = _0;
        this._1 = _1;
        this._2 = _2;
    }

    public T0 get_0() {
        return _0;
    }

    public T1 get_1() {
        return _1;
    }

    public T2 get_2() {
        return _2;
    }

    @Override
    public String toString() {
        return "(Tuple3 " + _0 + ", " + _1 + ", " + _2 + ")";
    }
}
