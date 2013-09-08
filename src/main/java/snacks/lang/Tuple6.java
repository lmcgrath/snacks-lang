package snacks.lang;

public class Tuple6<T0, T1, T2, T3, T4, T5> {

    private final T0 _0;
    private final T1 _1;
    private final T2 _2;
    private final T3 _3;
    private final T4 _4;
    private final T5 _5;

    public Tuple6(T0 _0, T1 _1, T2 _2, T3 _3, T4 _4, T5 _5) {
        this._0 = _0;
        this._1 = _1;
        this._2 = _2;
        this._3 = _3;
        this._4 = _4;
        this._5 = _5;
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

    public T3 get_3() {
        return _3;
    }

    public T4 get_4() {
        return _4;
    }

    public T5 get_5() {
        return _5;
    }

    @Override
    public String toString() {
        return "(Tuple6 " + _0 + ", " + _1 + ", " + _2 + ", " + _3 + ", " + _4 + ", " + _5 + ")";
    }
}
