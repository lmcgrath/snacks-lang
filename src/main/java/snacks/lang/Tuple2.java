package snacks.lang;

public class Tuple2<T0, T1> {

    private final T0 _0;
    private final T1 _1;

    public Tuple2(T0 _0, T1 _1) {
        this._0 = _0;
        this._1 = _1;
    }

    public T0 get_0() {
        return _0;
    }

    public T1 get_1() {
        return _1;
    }

    @Override
    public String toString() {
        return "(Tuple2 " + _0 + ", " + _1 + ")";
    }
}
