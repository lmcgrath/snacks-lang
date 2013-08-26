package snacks.lang.compiler.ast;

import java.util.Objects;
import org.apache.commons.lang.builder.EqualsBuilder;
import snacks.lang.SnacksException;
import snacks.lang.compiler.Type;

public class Function implements AstNode {

    private final String variable;
    private final AstNode expression;
    private final Type type;

    public Function(String variable, AstNode expression, Type type) {
        this.variable = variable;
        this.expression = expression;
        this.type = type;
    }

    @Override
    public void accept(AstVisitor visitor) throws SnacksException {
        visitor.visitFunction(this);
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (o instanceof Function) {
            Function other = (Function) o;
            return new EqualsBuilder()
                .append(variable, other.variable)
                .append(expression, other.expression)
                .append(type, other.type)
                .isEquals();
        } else {
            return false;
        }
    }

    public AstNode getExpression() {
        return expression;
    }

    @Override
    public Type getType() {
        return type;
    }

    public String getVariable() {
        return variable;
    }

    @Override
    public int hashCode() {
        return Objects.hash(variable, expression, type);
    }

    @Override
    public String toString() {
        return "(" + variable + " -> " + expression + "):" + type;
    }
}
