package snacks.lang;

public class Tuple0 {

    @Override
    public boolean equals(Object o) {
        return o == this || o instanceof Tuple0;
    }

    @Override
    public int hashCode() {
        return 1;
    }

    @Override
    public String toString() {
        return "(Tuple0)";
    }
}
