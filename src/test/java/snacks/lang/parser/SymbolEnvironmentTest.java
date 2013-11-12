package snacks.lang.parser;

import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static snacks.lang.Types.*;

import org.junit.Before;
import org.junit.Test;
import snacks.lang.SnacksRegistry;
import snacks.lang.Type;

public class SymbolEnvironmentTest {

    private SymbolEnvironment environment;

    @Before
    public void setUp() {
        SnacksRegistry registry = mock(SnacksRegistry.class);
        environment = new SymbolEnvironment(registry);
    }

    @Test
    public void functionShouldAcceptSimilarType() {
        assertThat(environment.unify(func(integerType(), booleanType()), func(integerType(), booleanType())), is(true));
    }

    @Test
    public void functionWithUnboundVariableShouldAcceptSimilarType() {
        assertThat(environment.unify(func(integerType(), var("B")), func(integerType(), booleanType())), is(true));
    }

    @Test
    public void unboundVariableShouldBeBoundAfterUnification() {
        Type function = func(integerType(), var("B"));
        environment.unify(function, func(integerType(), booleanType()));
        assertThat(function, equalTo(func(integerType(), var(booleanType()))));
    }

    @Test
    public void algebraicTypeShouldAcceptMemberType() {
        Type child = record("Node", asList(
            property("_0", integerType()),
            property("_1", algebraic("Tree", asList(
                simple("Leaf"),
                recur("Node")
            ))),
            property("_2", algebraic("Tree", asList(
                simple("Leaf"),
                recur("Node")
            )))
        ));
        Type parent = algebraic("Tree", asList(
            simple("Leaf"),
            child
        ));
        assertThat(environment.unify(parent, child), is(true));
    }
}
