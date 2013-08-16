package snacks.lang.compiler.syntax;

import java.util.Objects;
import beaver.Symbol;
import org.apache.commons.lang.builder.EqualsBuilder;

public class MapEntry extends Symbol {

    private final Symbol key;
    private final Symbol value;

    public MapEntry(Symbol key, Symbol value) {
        this.key = key;
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (o instanceof MapEntry) {
            MapEntry other = (MapEntry) o;
            return new EqualsBuilder()
                .append(key, other.key)
                .append(value, other.value)
                .isEquals();
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(key, value);
    }

    @Override
    public String toString() {
        return key + " => " + value;
    }
}
