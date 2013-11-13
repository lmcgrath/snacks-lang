package snacks.lang.atom;

import org.junit.Test;
import snacks.lang.runtime.AbstractRuntimeTest;

public class AtomTest extends AbstractRuntimeTest {

    @Test
    public void shouldSwapAtomValue() {
        run(
            "import snacks.lang.atom._",
            "next :: () -> Integer",
            "counter = Atom 0",
            "main = {",
            "    assert $ next () == 1",
            "    assert $ next () == 2",
            "}",
            "next = () -> swap! counter inc"
        );
    }

    @Test
    public void shouldSetAtomValue() {
        run(
            "import snacks.lang.atom._",
            "value = Atom 0",
            "main = {",
            "    set! value 3",
            "    assert $ ref value == 3",
            "    set! value 1",
            "    assert $ ref value == 1",
            "}"
        );
    }
}
