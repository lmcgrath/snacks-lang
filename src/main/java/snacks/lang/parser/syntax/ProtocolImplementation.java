package snacks.lang.parser.syntax;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import beaver.Symbol;
import com.google.common.collect.ImmutableList;
import org.apache.commons.lang.builder.EqualsBuilder;

public class ProtocolImplementation extends VisitableSymbol {

    private final String name;
    private final List<Symbol> arguments;
    private final List<Symbol> members;

    public ProtocolImplementation(String name, Collection<Symbol> arguments, Collection<Symbol> members) {
        this.name = name;
        this.arguments = ImmutableList.copyOf(arguments);
        this.members = ImmutableList.copyOf(members);
    }

    @Override
    public void accept(SyntaxVisitor visitor) {
        visitor.visitProtocolImplementation(this);
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (o instanceof ProtocolImplementation) {
            ProtocolImplementation other = (ProtocolImplementation) o;
            return new EqualsBuilder()
                .append(name, other.name)
                .append(arguments, other.arguments)
                .append(members, other.members)
                .isEquals();
        } else {
            return false;
        }
    }

    public List<Symbol> getArguments() {
        return arguments;
    }

    public List<Symbol> getMembers() {
        return members;
    }

    public String getName() {
        return name;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, arguments, members);
    }

    @Override
    public String toString() {
        return "(Implements " + name + " " + arguments + " " + members + ")";
    }
}
