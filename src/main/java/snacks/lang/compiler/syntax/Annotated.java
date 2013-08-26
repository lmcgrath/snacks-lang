package snacks.lang.compiler.syntax;

import static java.util.Arrays.asList;
import static org.apache.commons.lang.StringUtils.join;

import java.util.List;
import java.util.Objects;
import beaver.Symbol;
import org.apache.commons.lang.builder.EqualsBuilder;

public class Annotated extends Symbol implements Visitable {

    private final List<Symbol> annotations;
    private final Symbol expression;

    public Annotated(Symbol expression, Symbol... annotations) {
        this.annotations = asList(annotations);
        this.expression = expression;
    }

    @Override
    public void accept(SyntaxVisitor visitor) {
        visitor.visitAnnotated(this);
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (o instanceof Annotated) {
            Annotated other = (Annotated) o;
            return new EqualsBuilder()
                .append(annotations, other.annotations)
                .append(expression, other.expression)
                .isEquals();
        } else {
            return false;
        }
    }

    public List<Symbol> getAnnotations() {
        return annotations;
    }

    public Symbol getExpression() {
        return expression;
    }

    @Override
    public int hashCode() {
        return Objects.hash(annotations, expression);
    }

    @Override
    public String toString() {
        return expression + "<" + join(annotations, ", ") + ">";
    }
}
