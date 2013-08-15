package snacks.lang.compiler.syntax;

import beaver.Symbol;

public class SubImport extends Symbol {

    private final String module;
    private final String alias;

    public SubImport(String module, String alias) {
        this.module = module;
        this.alias = alias;
    }

    @Override
    public String toString() {
        if (alias.equals(module)) {
            return module;
        } else {
            return module + " as " + alias;
        }
    }
}
