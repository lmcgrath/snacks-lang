package snacks.lang.compiler.ast;

import static snacks.lang.compiler.Type.func;

import java.util.Objects;
import org.apache.commons.lang.builder.EqualsBuilder;
import snacks.lang.SnacksException;
import snacks.lang.compiler.Type;

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
    public Type getType() {
        return func(argument, result);
    }

    public String getVariable() {
        return variable;
    }

    @Override
    public int hashCode() {
        return Objects.hash(argument, result, variable, expression);
    }

    @Override
    public String toString() {
        return "(" + variable + ":" + argument + " :: " + result + " -> " + expression + "";
    }
}
