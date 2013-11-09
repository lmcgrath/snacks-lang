package snacks.lang;

import static snacks.lang.SnackKind.EXPRESSION;
import static snacks.lang.type.Types.SYMBOL_TYPE;
import static snacks.lang.type.Types.func;
import static snacks.lang.type.Types.var;

import snacks.lang.type.Type;

@Snack(name = "symbol", kind = EXPRESSION)
public class SymbolConstructor {

    private static SymbolConstructor instance;

    public static SymbolConstructor instance() {
        if (instance == null) {
            instance = new SymbolConstructor();
        }
        return instance;
    }

    @SnackType
    public static Type type() {
        return func(var("snacks.lang.symbol#a"), SYMBOL_TYPE);
    }

    public Object apply(Object value) {
        return Symbol.valueOf(value.toString());
    }
}
