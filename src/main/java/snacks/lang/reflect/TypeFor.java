package snacks.lang.reflect;

import static snacks.lang.SnackKind.EXPRESSION;
import static snacks.lang.SnackKind.TYPE;
import static snacks.lang.type.Types.SYMBOL_TYPE;
import static snacks.lang.type.Types.func;

import snacks.lang.MatchException;
import snacks.lang.Snack;
import snacks.lang.SnackType;
import snacks.lang.reflect.DeclarationName.ExpressionName;
import snacks.lang.reflect.DeclarationName.TypeName;
import snacks.lang.runtime.SnacksClassLoader;
import snacks.lang.type.Type;

@Snack(name = "typeFor", kind = EXPRESSION)
public class TypeFor {

    private static TypeFor instance;

    public static TypeFor instance() {
        if (instance == null) {
            instance = new TypeFor();
        }
        return instance;
    }

    @SnackType
    public static Type type() {
        return func(SYMBOL_TYPE, TypeInfo.type());
    }

    private final SnacksClassLoader loader;

    public TypeFor() {
        loader = new SnacksClassLoader(getClass().getClassLoader());
    }

    public Object apply(DeclarationName name) {
        if (name instanceof ExpressionName) {
            return loader.classOf(((ExpressionName) name).get_0().getValue(), EXPRESSION);
        } else if (name instanceof TypeName) {
            return loader.classOf(((TypeName) name).get_0().getValue(), TYPE);
        } else {
            throw new MatchException("Could not match " + name.getClass().getName());
        }
    }
}
