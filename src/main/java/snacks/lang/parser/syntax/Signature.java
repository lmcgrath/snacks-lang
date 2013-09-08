package snacks.lang.parser.syntax;

import java.util.Objects;
import beaver.Symbol;
import org.apache.commons.lang.builder.EqualsBuilder;

public class Signature extends Symbol implements Visitable {

    private final String identifier;
    private final Symbol type;

    public Signature(String identifier, Symbol type) {
        this.identifier = identifier;
        this.type = type;
    }

    @Override
    public void accept(SyntaxVisitor visitor) {
        visitor.visitSignature(this);
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (o instanceof Signature) {
            Signature other = (Signature) o;
            return new EqualsBuilder()
                .append(identifier, other.identifier)
                .append(type, other.type)
                .isEquals();
        } else {
            return false;
        }
    }

    public String getIdentifier() {
        return identifier;
    }

    public Symbol getType() {
        return type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(identifier, type);
    }

    @Override
    public String toString() {
        return "(" + identifier + " :: " + type + ")";
    }
}
