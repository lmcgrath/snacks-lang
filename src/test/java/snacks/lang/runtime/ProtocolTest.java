package snacks.lang.runtime;

import org.junit.Ignore;
import org.junit.Test;
import snacks.lang.util.SnacksTest;

public class ProtocolTest extends SnacksTest {

    @Ignore("WIP")
    @Test
    public void shouldCreateProtocol() {
        run(
            "protocol Equitable a where",
            "    (==) :: a -> a -> Boolean",
            "    (!=) :: a -> a -> Boolean",
            "    (!=) = (x y) -> not $ x == y",
            "end",
            "data Tree a = Leaf | Node a (Tree a) (Tree a)",
            "implement Equitable (Tree a) where",
            "    (==) = ?(Leaf, Leaf) -> True",
            "    (==) = ?(Node x l1 r1, Node y l2 r2) -> x == y and l1 == l2 and r1 == r2",
            "    (==) = ?(_, _) -> False",
            "end",
            "main = () -> {",
            "    assert $ (Node 3 Leaf Leaf) == (Node 3 Leaf Leaf)",
            "    assert $ (Node 4 Leaf Leaf) != (Node 4 (Node 1 Leaf Leaf) Leaf)",
            "}"
        );
    }
}
