package snacks.lang;

public class Tuple1<T0> {

    private final T0 _0;

    public Tuple1(T0 _0) {
        this._0 = _0;
    }

    public T0 get_0() {
        return _0;
    }

    @Override
    public String toString() {
        return "(Tuple1 " + _0 + ")";
    }
}
