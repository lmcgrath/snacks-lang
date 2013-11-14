package snacks.lang;

import static org.apache.commons.lang.StringEscapeUtils.escapeJava;
import static snacks.lang.SnackKind.EXPRESSION;
import static snacks.lang.SnackKind.TYPE;
import static snacks.lang.Types.func;
import static snacks.lang.Types.symbolType;
import static snacks.lang.Types.var;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Snack(name = "Symbol", kind = TYPE)
public class Symbol {

    @SnackType
    public static Type type() {
        return symbolType();
    }

    private static final Map<String, Symbol> symbols = new HashMap<>();
    private static final Object lock = new Object();

    public static Symbol valueOf(String value) {
        synchronized (lock) {
            if (!symbols.containsKey(value)) {
                synchronized (lock) {
                    symbols.put(value, new Symbol(value));
                }
            }
        }
        return symbols.get(value);
    }

    private final String value;

    private Symbol(String value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        return o == this || o instanceof Symbol && Objects.equals(value, ((Symbol) o).value);
    }

    public String getValue() {
        return value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return ":'" + escapeJava(value) + "'";
    }

    @Snack(name = "symbol", kind = EXPRESSION)
    public static class Constructor {

        private static Constructor instance;

        public static Object instance() {
            if (instance == null) {
                instance = new Constructor();
            }
            return instance;
        }

        @SnackType
        public static Type type() {
            return func(var("snacks.lang.symbol#a"), symbolType());
        }

        public Object apply(Object value) {
            return valueOf(value.toString());
        }
    }
}
