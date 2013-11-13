package snacks.lang.atom;

import static snacks.lang.SnackKind.EXPRESSION;
import static snacks.lang.Types.func;
import static snacks.lang.atom.Atom.argType;
import static snacks.lang.atom.Atom.atomType;

import snacks.lang.Snack;
import snacks.lang.SnackType;
import snacks.lang.Type;

@Snack(name = "ref", kind = EXPRESSION)
public class Ref {

    private static Ref instance;

    public static Ref instance() {
        if (instance == null) {
            instance = new Ref();
        }
        return instance;
    }

    @SnackType
    public static Type type() {
        return func(atomType(), argType());
    }

    public Object apply(Atom<Object> atom) {
        return atom.getRef();
    }
}
