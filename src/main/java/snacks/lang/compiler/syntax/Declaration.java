package snacks.lang.compiler.syntax;

import static java.util.Arrays.asList;
import static org.apache.commons.lang.StringUtils.join;

import java.util.List;
import beaver.Symbol;

public class Declaration extends Symbol {

    private final String name;
    private final Symbol body;
    private final List<Symbol> annotations;

    public Declaration(String name, Symbol body, Symbol... annotations) {
        this.name = name;
        this.body = body;
        this.annotations = asList(annotations);
    }

    @Override
    public String toString() {
        String value = "(" + name + " = " + body + ")";
        if (!annotations.isEmpty()) {
            value += "<" + join(annotations, ", ") + ">";
        }
        return value;
    }
}
