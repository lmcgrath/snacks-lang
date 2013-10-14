package snacks.lang.type;

import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static snacks.lang.type.Types.algebraic;
import static snacks.lang.type.Types.property;
import static snacks.lang.type.Types.record;
import static snacks.lang.type.Types.recur;
import static snacks.lang.type.Types.simple;

import org.junit.Before;
import org.junit.Test;

public class AlgebraicTypeTest {

    private Type tree;
    private Type node;

    @Before
    public void setUp() {
        tree = algebraic("Tree", asList(
            simple("Leaf"),
            record("Node", asList(
                property("_0", simple("Integer")),
                property("_1", recur("Tree")),
                property("_2", recur("Tree"))
            ))
        ));
        node = record("Node", asList(
            property("_0", simple("Integer")),
            property("_1", algebraic("Tree", asList(
                simple("Leaf"),
                recur("Node")
            ))),
            property("_2", algebraic("Tree", asList(
                simple("Leaf"),
                recur("Node")
            )))
        ));
    }

    @Test
    public void expectationOfAlgebraicTypeShouldAcceptMemberType() {
        assertThat(node.accepts(tree, null), is(true));
    }

    @Test
    public void expectationOfRecursiveMemberTypeShouldNotAcceptAlgebraicSuperType() {
        assertThat(tree.accepts(node, null), is(false));
    }
}
