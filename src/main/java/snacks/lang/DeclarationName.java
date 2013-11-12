package snacks.lang;

import static java.util.Arrays.asList;
import static snacks.lang.SnackKind.EXPRESSION;
import static snacks.lang.SnackKind.TYPE;
import static snacks.lang.Types.symbolType;
import static snacks.lang.Types.func;
import static snacks.lang.Types.property;
import static snacks.lang.Types.record;

import java.util.Objects;

@Snack(name = "DeclarationName", kind = TYPE)
public abstract class DeclarationName {

    private DeclarationName() {
        // intentionally empty
    }

    @Snack(name = "TypeName", kind = EXPRESSION)
    public static final class TypeNameConstructor {

        private static TypeNameConstructor instance;

        public static TypeNameConstructor instance() {
            if (instance == null) {
                instance = new TypeNameConstructor();
            }
            return instance;
        }

        @SnackType
        public static Type type() {
            return func(symbolType(), TypeName.type());
        }

        public Object apply(Symbol name) {
            return new TypeName(name);
        }
    }

    @Snack(name = "TypeName", kind = TYPE)
    public static final class TypeName extends DeclarationName {

        @SnackType
        public static Type type() {
            return record("snacks.lang.reflect.TypeName", asList(
                property("_0", symbolType())
            ));
        }

        private final Symbol _0;

        public TypeName(Symbol _0) {
            this._0 = _0;
        }

        @Override
        public boolean equals(Object o) {
            return o == this || o instanceof TypeName && Objects.equals(_0, ((TypeName) o)._0);
        }

        public Symbol get_0() {
            return _0;
        }

        @Override
        public int hashCode() {
            return Objects.hash(_0);
        }

        @Override
        public String toString() {
            return "TypeName(" + _0 + ")";
        }
    }

    @Snack(name = "ExpressionName", kind = EXPRESSION)
    public static final class ExpressionNameConstructor {

        private static ExpressionNameConstructor instance;

        public static ExpressionNameConstructor instance() {
            if (instance == null) {
                instance = new ExpressionNameConstructor();
            }
            return instance;
        }

        @SnackType
        public static Type type() {
            return func(symbolType(), ExpressionName.type());
        }

        public Object apply(Symbol name) {
            return new ExpressionName(name);
        }
    }

    @Snack(name = "ExpressionName", kind = TYPE)
    public static final class ExpressionName extends DeclarationName {

        @SnackType
        public static Type type() {
            return record("snacks.lang.reflect.ExpressionName", asList(
                property("_0", symbolType())
            ));
        }

        private final Symbol _0;

        public ExpressionName(Symbol _0) {
            this._0 = _0;
        }

        @Override
        public boolean equals(Object o) {
            return o == this || o instanceof ExpressionName && Objects.equals(_0, ((ExpressionName) o)._0);
        }

        public Symbol get_0() {
            return _0;
        }

        @Override
        public int hashCode() {
            return Objects.hash(_0);
        }

        @Override
        public String toString() {
            return "ExpressionName(" + _0 + ")";
        }
    }
}
