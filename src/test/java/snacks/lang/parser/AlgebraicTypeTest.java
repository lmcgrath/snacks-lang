package snacks.lang.parser;

import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static snacks.lang.SnackKind.TYPE;
import static snacks.lang.Types.*;

import org.junit.Before;
import org.junit.Test;
import snacks.lang.SnacksRegistry;
import snacks.lang.Type;

public class AlgebraicTypeTest {

    private SnacksRegistry registry;
    private SymbolEnvironment environment;
    private Type tree;
    private Type node;

    @Before
    public void setUp() {
        registry = mock(SnacksRegistry.class);
        environment = new SymbolEnvironment(registry);
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
        doReturn(tree).when(registry).typeOf("Tree", TYPE);
        doReturn(node).when(registry).typeOf("Node", TYPE);
    }

    @Test
    public void expectationOfAlgebraicTypeShouldAcceptMemberType() {
        assertThat(environment.unify(tree, node), is(true));
    }
}
