package snacks.lang;

import org.junit.Ignore;
import org.junit.Test;
import snacks.lang.util.SnacksTest;

@Ignore("WIP")
public class MapTest extends SnacksTest {

    @Test
    public void shouldCreateMap() {
        run(
            "map = { 1 => 'one', 2 => 'two', 3 => 'three' }",
            "main = () -> assert $ string map == '{ 1 => one, 2 => two, 3 => three }'"
        );
    }

    @Ignore("WIP")
    @Test
    public void shouldInsertElementIntoMap() {
        run(
            "insert :: Map k v -> k -> v -> Map k v",

            "e = MapEntry",
            "n = EmptyMap",
            "m = e 2 2 'two' (e 1 1 'one' n n) n",

            "main = {",
            "    assert $ insert m 3 'three' == e 3 2 'two' (e 1 1 'one' n n) (e 1 3 'three' n n)",
            "}",

            "insert = ?(MapEntry {}, k, v) -> e 3 2 'two' (e 1 1 'one' n n) (e 1 3 'three' n n)"
        );
    }
}
