package snacks.lang.parser;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static snacks.lang.Types.simple;
import static snacks.lang.Types.var;

import org.junit.Before;
import org.junit.Test;
import snacks.lang.SnacksRegistry;
import snacks.lang.Type;

public class VariableTypeTest {

    private SymbolEnvironment environment;
    private Type variableType;
    private Type concreteType;

    @Before
    public void setUp() {
        environment = new SymbolEnvironment(mock(SnacksRegistry.class));
        variableType = var("a");
        concreteType = simple("Integer");
    }

    @Test
    public void expectationOfConcreteTypeShouldBindVariableType() {
        assertTrue(environment.unify(variableType, concreteType));
        assertThat(variableType, equalTo(var(simple("Integer"))));
    }

    @Test
    public void expectationOfVariableTypeShouldBindVariableType() {
        assertTrue(environment.unify(concreteType, variableType));
        assertThat(variableType, equalTo(var(simple("Integer"))));
    }

    @Test
    public void variableTypeShouldNotRebind() {
        Type differentType = simple("Boolean");
        environment.unify(variableType, concreteType);
        assertFalse(environment.unify(variableType, differentType));
    }

    @Test
    public void variableShouldBindToVariable() {
        Type differentVariable = var("b");
        assertTrue(environment.unify(variableType, differentVariable));
    }
}
