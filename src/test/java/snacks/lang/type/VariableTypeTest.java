package snacks.lang.type;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static snacks.lang.type.Types.simple;
import static snacks.lang.type.Types.var;

import org.junit.Before;
import org.junit.Test;

public class VariableTypeTest {

    private Type variableType;
    private Type concreteType;

    @Before
    public void setUp() {
        variableType = var("a");
        concreteType = simple("Integer");
    }

    @Test
    public void expectationOfConcreteTypeShouldBindVariableType() {
        assertTrue(variableType.accepts(concreteType));
        assertThat(variableType, equalTo(var(simple("Integer"))));
    }

    @Test
    public void expectationOfVariableTypeShouldBindVariableType() {
        assertTrue(concreteType.accepts(variableType));
        assertThat(variableType, equalTo(var(simple("Integer"))));
    }

    @Test
    public void variableTypeShouldNotRebind() {
        Type differentType = simple("Boolean");
        variableType.accepts(concreteType);
        assertFalse(variableType.accepts(differentType));
    }

    @Test
    public void variableShouldBindToVariable() {
        Type differentVariable = var("b");
        assertTrue(variableType.accepts(differentVariable));
    }
}
