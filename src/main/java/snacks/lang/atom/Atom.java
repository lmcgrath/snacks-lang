package snacks.lang.atom;

import static java.util.Arrays.asList;
import static snacks.lang.SnackKind.EXPRESSION;
import static snacks.lang.SnackKind.TYPE;
import static snacks.lang.Types.func;
import static snacks.lang.Types.property;
import static snacks.lang.Types.record;
import static snacks.lang.Types.var;

import java.util.concurrent.atomic.AtomicReference;
import snacks.lang.Snack;
import snacks.lang.SnackType;
import snacks.lang.Type;

@Snack(name = "Atom", kind = TYPE, arguments = "snacks.lang.atom.Atom#a")
public final class Atom<T> {

    public static Type argType() {
        return var("snacks.lang.atom.Atom#a");
    }

    @SnackType
    public static Type atomType() {
        return record("snacks.lang.atom.Atom", asList(argType()), asList(
            property("ref", argType())
        ));
    }

    public final AtomicReference<T> state;

    public Atom(T state) {
        this.state = new AtomicReference<>(state);
    }

    public T getRef() {
        return state.get();
    }

    public boolean setRef(T ref) {
        return state.compareAndSet(getRef(), ref);
    }

    @Snack(name = "Atom", kind = EXPRESSION)
    public static class Constructor {

        private static Constructor instance;

        public static Constructor instance() {
            if (instance == null) {
                instance = new Constructor();
            }
            return instance;
        }

        @SnackType
        public static Type type() {
            return func(argType(), atomType());
        }

        public Object apply(Object value) {
            return new Atom<>(value);
        }
    }
}
