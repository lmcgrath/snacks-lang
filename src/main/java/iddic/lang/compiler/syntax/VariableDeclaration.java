package iddic.lang.compiler.syntax;

import static iddic.lang.util.StringUtil.stringify;

import java.util.Objects;

public class VariableDeclaration extends SyntaxNode {

    private final String variable;
    private final SyntaxNode expression;

    public VariableDeclaration(String variable, SyntaxNode expression) {
        this.variable = variable;
        this.expression = expression;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (o instanceof VariableDeclaration) {
            VariableDeclaration other = (VariableDeclaration) o;
            return Objects.equals(variable, other.variable)
                && Objects.equals(expression, other.expression);
        } else {
            return false;
        }
    }

    public SyntaxNode getExpression() {
        return expression;
    }

    public String getVariable() {
        return variable;
    }

    @Override
    public int hashCode() {
        return Objects.hash(variable, expression);
    }

    @Override
    public String toString() {
        return stringify(this, variable, expression);
    }
}
