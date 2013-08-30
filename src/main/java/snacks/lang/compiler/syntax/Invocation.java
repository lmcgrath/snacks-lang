package snacks.lang.compiler.syntax;

import java.util.Objects;
import beaver.Symbol;

public class Invocation extends Symbol implements Visitable {

    private final Symbol expression;

    public Invocation(Symbol expression) {
        this.expression = expression;
    }

    @Override
    public void accept(SyntaxVisitor visitor) {
        visitor.visitInvocation(this);
    }

    @Override
    public boolean equals(Object o) {
        return o == this || o instanceof Invocation && Objects.equals(expression, ((Invocation) o).expression);
    }

    public Symbol getExpression() {
        return expression;
    }

    @Override
    public int hashCode() {
        return Objects.hash(expression);
    }

    @Override
    public String toString() {
        return "(invoke " + expression.toString() + ")";
    }
}