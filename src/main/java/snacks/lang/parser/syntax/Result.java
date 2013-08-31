package snacks.lang.parser.syntax;

import java.util.Objects;
import beaver.Symbol;

public class Result extends Symbol implements Visitable {

    private final Symbol expression;

    public Result(Symbol expression) {
        this.expression = expression;
    }

    @Override
    public void accept(SyntaxVisitor visitor) {
        visitor.visitResult(this);
    }

    @Override
    public boolean equals(Object o) {
        return o == this || o instanceof Result && Objects.equals(expression, ((Result) o).expression);
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
        return "(return " + expression + ")";
    }
}
