package snacks.lang.compiler.syntax;

import static java.util.Arrays.asList;
import static org.apache.commons.lang.StringUtils.join;

import java.util.List;
import beaver.Symbol;

public class Annotation extends Symbol {

    private final List<String> name;
    private final Symbol value;

    public Annotation(String[] name, Symbol value) {
        this.name = asList(name);
        this.value = value;
    }

    @Override
    public String toString() {
        return "@" + join(name, '.') + (value instanceof NothingLiteral ? "" : "(" + value + ")");
    }
}
