package snacks.lang;

import org.junit.Test;
import snacks.lang.util.SnacksTest;

public class MapTest extends SnacksTest {

    @Test
    public void shouldCreateMap() {
        run(
                "map = { 1 => 'one', 2 => 'two', 3 => 'three' }",
                "main = () -> assert $ string map == '{ 1 => one, 2 => two, 3 => three }'"
        );
    }

    @Test
    public void shouldInsertElementIntoMap() {
        run(
                "insert :: Map k v -> k -> v -> Map k v",

                "main = {",
                "    assert $ insert {:} 3 'three' == { 3 => 'three' }",
                "    assert $ insert {:} :bacon 'delicious' == { bacon: 'delicious' }",
                "}",

                "insert = ?(EmptyMap, k, v) -> MapEntry { key = k, value = v, left = EmptyMap, right = EmptyMap }"
        );
    }
}
