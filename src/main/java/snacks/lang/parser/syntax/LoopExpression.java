package snacks.lang.parser.syntax;

import java.util.Objects;
import beaver.Symbol;
import org.apache.commons.lang.builder.EqualsBuilder;

public class LoopExpression extends VisitableSymbol {

    private final Symbol condition;
    private final Symbol body;

    public LoopExpression(Symbol condition, Symbol body) {
        this.condition = condition;
        this.body = body;
    }

    @Override
    public void accept(SyntaxVisitor visitor) {
        visitor.visitLoopExpression(this);
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (o instanceof LoopExpression) {
            LoopExpression other = (LoopExpression) o;
            return new EqualsBuilder()
                .append(condition, other.condition)
                .append(body, other.body)
                .isEquals();
        } else {
            return false;
        }
    }

    public Symbol getBody() {
        return body;
    }

    public Symbol getCondition() {
        return condition;
    }

    @Override
    public int hashCode() {
        return Objects.hash(condition, body);
    }

    @Override
    public String toString() {
        return "(while " + condition + " do " + body + ")";
    }
}
