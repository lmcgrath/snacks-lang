package snacks.lang.parser.syntax;

import java.util.Objects;
import beaver.Symbol;
import org.apache.commons.lang.builder.EqualsBuilder;

public class IteratorLoop extends VisitableSymbol {

    private final String variable;
    private final Symbol expression;
    private final Symbol action;

    public IteratorLoop(String variable, Symbol expression, Symbol action) {
        this.variable = variable;
        this.expression = expression;
        this.action = action;
    }

    @Override
    public void accept(SyntaxVisitor visitor) {
        visitor.visitIteratorLoop(this);
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (o instanceof IteratorLoop) {
            IteratorLoop other = (IteratorLoop) o;
            return new EqualsBuilder()
                .append(variable, other.variable)
                .append(expression, other.expression)
                .append(action, other.action)
                .isEquals();
        } else {
            return false;
        }
    }

    public Symbol getAction() {
        return action;
    }

    public Symbol getExpression() {
        return expression;
    }

    public String getVariable() {
        return variable;
    }

    @Override
    public int hashCode() {
        return Objects.hash(variable, expression, action);
    }

    @Override
    public String toString() {
        return "(for " + variable + " in " + expression + " do " + action + ")";
    }
}
