package snacks.lang.compiler.syntax;

import static java.util.Arrays.asList;
import static org.apache.commons.lang.StringUtils.join;

import java.util.List;
import java.util.Objects;
import beaver.Symbol;

public class Module extends Symbol {

    private final List<Symbol> declarations;

    public Module(Symbol... declarations) {
        this.declarations = asList(declarations);
    }

    @Override
    public boolean equals(Object o) {
        return o == this || o instanceof Module && Objects.equals(declarations, ((Module) o).declarations);
    }

    @Override
    public int hashCode() {
        return Objects.hash(declarations);
    }

    @Override
    public String toString() {
        return "(" + join(declarations, "; ") + ")";
    }
}
