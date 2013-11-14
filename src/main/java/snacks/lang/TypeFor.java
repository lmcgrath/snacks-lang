package snacks.lang;

import static java.lang.Thread.currentThread;
import static java.util.Arrays.asList;
import static snacks.lang.SnackKind.EXPRESSION;
import static snacks.lang.SnackKind.TYPE;
import static snacks.lang.Types.algebraic;
import static snacks.lang.Types.func;

import snacks.lang.DeclarationName.ExpressionName;
import snacks.lang.DeclarationName.TypeName;
import snacks.lang.runtime.SnacksClassLoader;

@Snack(name = "typeFor", kind = EXPRESSION)
public class TypeFor {

    private static TypeFor instance;

    public static Object instance() {
        if (instance == null) {
            instance = new TypeFor();
        }
        return instance;
    }

    @SnackType
    public static Type type() {
        return func(
            algebraic("snacks.lang.DeclarationName", asList(
                TypeName.type(),
                ExpressionName.type()
            )),
            Type.type()
        );
    }

    public Object apply(DeclarationName name) {
        SnacksClassLoader loader = getLoader();
        if (name instanceof ExpressionName) {
            return loader.typeOf(((ExpressionName) name).get_0().getValue(), EXPRESSION);
        } else if (name instanceof TypeName) {
            return loader.typeOf(((TypeName) name).get_0().getValue(), TYPE);
        } else {
            throw new MatchException("Could not match " + name.getClass().getName());
        }
    }

    public SnacksClassLoader getLoader() {
        ClassLoader loader = currentThread().getContextClassLoader();
        if (loader instanceof SnacksClassLoader) {
            return (SnacksClassLoader) loader;
        } else {
            throw new SnacksException("Context classloader is not an instance of SnacksClassLoader");
        }
    }
}
