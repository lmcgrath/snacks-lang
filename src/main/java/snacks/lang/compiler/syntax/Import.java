package snacks.lang.compiler.syntax;

import static java.util.Arrays.asList;
import static org.apache.commons.lang.StringUtils.join;

import java.util.List;
import beaver.Symbol;

public class Import extends Symbol {

    private final List<String> module;
    private final String alias;

    public Import(String[] module, String alias) {
        this.module = asList(module);
        this.alias = alias;
    }

    @Override
    public String toString() {
        if (alias.equals(module.get(module.size() - 1))) {
            return "(import " + join(module, '.') + ")";
        } else {
            return "(import " + join(module, '.') + " as " + alias + ")";
        }
    }
}
