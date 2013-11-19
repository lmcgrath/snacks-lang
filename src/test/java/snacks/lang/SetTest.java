package snacks.lang;

import org.junit.Test;
import snacks.lang.util.SnacksTest;

public class SetTest extends SnacksTest {

    @Test
    public void shouldCreateMap() {
        run(
            "set = { 'one', 'two', 'three' }",
            "main = {",
            "    say set",
            "    assert $ string set == '{ one, two, three }'",
            "    assert $ string EmptySet == '{,}'",
            "}"
        );
    }

    @Test
    public void shouldInsertElementIntoSet() {
        run(
            "insert :: Set a -> a -> Set a",

            "main = {",
            "    assert $ insert {,} 'three' == { 'three', }",
            "    assert $ insert {,} 'bacon' == { 'bacon', }",
            "}",

            "insert = ?(EmptySet, x) -> SetEntry { hash = hashOf x, elements = [x], left = EmptySet, right = EmptySet }"
        );
    }
}
