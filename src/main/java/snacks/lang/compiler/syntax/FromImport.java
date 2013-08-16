package snacks.lang.compiler.syntax;

import static java.util.Arrays.asList;
import static org.apache.commons.lang.StringUtils.join;

import java.util.List;
import beaver.Symbol;

public class FromImport extends Symbol {

    private final List<String> module;
    private final List<Symbol> subImports;

    public FromImport(String[] module, Symbol... subImports) {
        this.module = asList(module);
        this.subImports = asList(subImports);
    }

    @Override
    public String toString() {
        return "(from " + join(module, '.') + " import " + join(subImports, ", ") + ")";
    }
}
