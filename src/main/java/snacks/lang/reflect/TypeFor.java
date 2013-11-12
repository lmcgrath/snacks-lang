package snacks.lang.reflect;

import static java.lang.Thread.currentThread;
import static java.util.Arrays.asList;
import static snacks.lang.SnackKind.EXPRESSION;
import static snacks.lang.SnackKind.TYPE;
import static snacks.lang.reflect.TypeTransformer.transform;
import static snacks.lang.type.Types.algebraic;
import static snacks.lang.type.Types.func;

import snacks.lang.MatchException;
import snacks.lang.Snack;
import snacks.lang.SnackType;
import snacks.lang.SnacksException;
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
        return func(
            algebraic("snacks.lang.reflect.DeclarationName", asList(
                TypeName.type(),
                ExpressionName.type()
            )),
            TypeInfo.type()
        );
    }

    public Object apply(DeclarationName name) {
        SnacksClassLoader loader = getLoader();
        if (name instanceof ExpressionName) {
            return transform(loader.typeOf(((ExpressionName) name).get_0().getValue(), EXPRESSION));
        } else if (name instanceof TypeName) {
            return transform(loader.typeOf(((TypeName) name).get_0().getValue(), TYPE));
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
