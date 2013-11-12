package snacks.lang.parser;

import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static snacks.lang.Types.*;
import static snacks.lang.Types.property;
import static snacks.lang.Types.record;
import static snacks.lang.Types.simple;
import static snacks.lang.Types.var;

import org.junit.Before;
import org.junit.Test;
import snacks.lang.SnacksRegistry;
import snacks.lang.Type;

public class AlgebraicTypeWithArgumentsTest {

    private SymbolEnvironment environment;

    @Before
    public void setUp() {
        environment = new SymbolEnvironment(mock(SnacksRegistry.class));
    }

    @Test
    public void shouldUnifyGenericType() {
        Type actual = func(
            record("Just", asList(var("a")), asList(property("_0", var("a")))),
            var("a")
        );
        Type accepted = func(
            algebraic("Maybe", asList(var("a")), asList(
                simple("Nothing"),
                record("Just", asList(property("_0", var("a"))))
            )),
            var("a")
        );
        assertThat(environment.unify(accepted, actual), is(true));
    }

    @Test
    public void shouldNotUnifyNonMatchingGenericType() {
        Type actual = func(
            record("Just", asList(property("_0", simple("String")))),
            simple("String")
        );
        Type accepted = func(
            algebraic("Maybe", asList(var(simple("Integer"))), asList(
                simple("Nothing"),
                record("Just", asList(property("_0", simple("Integer"))))
            )),
            simple("Integer")
        );
        assertThat(environment.unify(accepted, actual), is(false));
    }
}
