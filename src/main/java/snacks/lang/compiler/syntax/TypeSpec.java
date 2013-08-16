package snacks.lang.compiler.syntax;

import static java.util.Arrays.asList;
import static org.apache.commons.lang.StringUtils.join;

import java.util.List;
import java.util.Objects;
import beaver.Symbol;

public class TypeSpec extends Symbol {

    private final List<String> type;

    public TypeSpec(String[] type) {
        this.type = asList(type);
    }

    @Override
    public boolean equals(Object o) {
        return o == this || o instanceof TypeSpec && Objects.equals(type, ((TypeSpec) o).type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type);
    }

    @Override
    public String toString() {
        return join(type, '.');
    }
}
