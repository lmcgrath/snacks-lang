package snacks.lang.compiler.syntax;

import static java.util.Arrays.asList;
import static org.apache.commons.lang.StringUtils.join;

import java.util.List;
import beaver.Symbol;

public class Module extends Symbol {

    private final List<Symbol> declarations;

    public Module(Symbol... declarations) {
        this.declarations = asList(declarations);
    }

    @Override
    public String toString() {
        return "(" + join(declarations, "; ") + ")";
    }
}
