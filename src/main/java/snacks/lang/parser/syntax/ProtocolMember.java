package snacks.lang.parser.syntax;

import java.util.Objects;
import beaver.Symbol;
import org.apache.commons.lang.builder.EqualsBuilder;

public class ProtocolMember extends VisitableSymbol {

    private final String name;
    private final Symbol body;

    public ProtocolMember(String name, Symbol body) {
        this.name = name;
        this.body = body;
    }

    @Override
    public void accept(SyntaxVisitor visitor) {
        throw new UnsupportedOperationException(); // TODO
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (o instanceof ProtocolMember) {
            ProtocolMember other = (ProtocolMember) o;
            return new EqualsBuilder()
                .append(name, other.name)
                .append(body, other.body)
                .isEquals();
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, body);
    }

    @Override
    public String toString() {
        return "(ProtocolMember " + name + " " + body + ")";
    }
}
