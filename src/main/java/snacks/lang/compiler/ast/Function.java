package snacks.lang.compiler.ast;

import java.util.Objects;
import org.apache.commons.lang.builder.EqualsBuilder;
import snacks.lang.SnacksException;
import snacks.lang.compiler.Type;
import snacks.lang.compiler.TypeOperator;

public class Function implements AstNode {

    private final Type argument;
    private final Type result;
    private final String variable;
    private final AstNode expression;

    public Function(Type argument, Type result, String variable, AstNode expression) {
        this.argument = argument;
        this.result = result;
        this.variable = variable;
        this.expression = expression;
    }

    @Override
    public <R, S> R accept(AstVisitor<R, S> visitor, S state) throws SnacksException {
        return visitor.visitFunction(this, state);
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (o instanceof Function) {
            Function other = (Function) o;
            return new EqualsBuilder()
                .append(argument, other.argument)
                .append(result, other.result)
                .append(variable, other.variable)
                .append(expression, other.expression)
                .isEquals();
        } else {
            return false;
        }
    }

    public AstNode getExpression() {
        return expression;
    }

    @Override
    public Reference getReference() {
        throw new IllegalStateException();
    }

    @Override
    public Type getType() {
        return TypeOperator.func(argument, result);
    }

    public String getVariable() {
        return variable;
    }

    @Override
    public int hashCode() {
        return Objects.hash(argument, result, variable, expression);
    }

    @Override
    public boolean isFunction() {
        return true;
    }

    @Override
    public boolean isReference() {
        return false;
    }

    @Override
    public String toString() {
        return "(" + variable + ":" + argument + " :: " + result + " -> " + expression + "";
    }
}
