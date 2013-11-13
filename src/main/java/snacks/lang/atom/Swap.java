package snacks.lang.atom;

import static org.apache.commons.lang.reflect.MethodUtils.getMatchingAccessibleMethod;
import static snacks.lang.SnackKind.EXPRESSION;
import static snacks.lang.Types.func;
import static snacks.lang.atom.Atom.argType;
import static snacks.lang.atom.Atom.atomType;

import java.lang.reflect.Method;
import snacks.lang.Snack;
import snacks.lang.SnackType;
import snacks.lang.SnacksException;
import snacks.lang.Type;

@Snack(name = "swap!", kind = EXPRESSION)
public class Swap {

    private static Swap instance;

    public static Swap instance() {
        if (instance == null) {
            instance = new Swap();
        }
        return instance;
    }

    @SnackType
    public static Type type() {
        // swap! :: Atom a -> (a -> a) -> a
        return func(atomType(), func(func(argType(), argType()), argType()));
    }

    public Object apply(Atom<Object> atom) {
        return new Closure(atom);
    }

    public static final class Closure {

        private final Atom<Object> atom;

        public Closure(Atom<Object> atom) {
            this.atom = atom;
        }

        public Object apply(Object function) {
            // TODO make this work without reflection
            Method method = getMatchingAccessibleMethod(function.getClass(), "apply", new Class<?>[] { atom.getRef().getClass() });
            while (true) {
                try {
                    Object newValue = method.invoke(function, atom.getRef());
                    if (atom.setRef(newValue)) {
                        return newValue;
                    }
                } catch (ReflectiveOperationException exception) {
                    throw new SnacksException(exception);
                }
            }
        }
    }
}
