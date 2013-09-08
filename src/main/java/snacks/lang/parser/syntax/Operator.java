package snacks.lang.parser.syntax;

import java.util.Objects;
import beaver.Symbol;
import org.apache.commons.lang.builder.EqualsBuilder;
import snacks.lang.Fixity;

public class Operator extends Symbol implements Visitable {

    private final Fixity fixity;
    private final int precedence;
    private final String name;

    public Operator(Fixity fixity, int precedence, String name) {
        this.fixity = fixity;
        this.precedence = precedence;
        this.name = name;
    }

    @Override
    public void accept(SyntaxVisitor visitor) {
        visitor.visitOperator(this);
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (o instanceof Operator) {
            Operator other = (Operator) o;
            return new EqualsBuilder()
                .append(fixity, other.fixity)
                .append(precedence, other.precedence)
                .append(name, other.name)
                .isEquals();
        } else {
            return false;
        }
    }

    public Fixity getFixity() {
        return fixity;
    }

    public String getName() {
        return name;
    }

    public int getPrecedence() {
        return precedence;
    }

    @Override
    public int hashCode() {
        return Objects.hash(fixity, precedence, name);
    }
}
