package snacks.lang;

import org.junit.Test;
import snacks.lang.util.SnacksTest;

public class ListTest extends SnacksTest {

    @Test
    public void shouldCreateList() {
        run("main = () -> assert $ string [1, 2, 3] == '[1, 2, 3]'");
    }

    @Test
    public void shouldCreateEmptyList() {
        run("main = () -> assert $ string [] == '[]'");
    }

    @Test
    public void shouldCreateListOfTuples() {
        run("main = () -> assert $ string [(1, 2), (3, 4)] == '[(1, 2), (3, 4)]'");
    }
}
