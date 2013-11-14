package snacks.lang.atom;

import static snacks.lang.SnackKind.EXPRESSION;
import static snacks.lang.Types.func;
import static snacks.lang.atom.Atom.argType;
import static snacks.lang.atom.Atom.atomType;

import snacks.lang.Snack;
import snacks.lang.SnackType;
import snacks.lang.Type;

@Snack(name = "set!", kind = EXPRESSION)
public class Set {

    private static Set instance;

    public static Object instance() {
        if (instance == null) {
            instance = new Set();
        }
        return instance;
    }

    @SnackType
    public static Type type() {
        return func(atomType(), func(argType(), argType()));
    }

    public Object apply(Atom<Object> atom) {
        return new Closure(atom);
    }

    public static final class Closure {

        private final Atom<Object> atom;

        public Closure(Atom<Object> atom) {
            this.atom = atom;
        }

        public Object apply(Object value) {
            atom.setRef(value);
            return value;
        }
    }
}
