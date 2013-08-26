package snacks.lang.compiler.syntax;

import java.util.Objects;
import beaver.Symbol;
import org.apache.commons.lang.builder.EqualsBuilder;
import snacks.lang.SnacksException;

public class Argument extends Symbol implements Visitable {

    private final String name;
    private final Symbol type;

    public Argument(String name, Symbol type) {
        this.name = name;
        this.type = type;
    }

    @Override
    public void accept(SyntaxVisitor visitor) throws SnacksException {
        visitor.visitArgument(this);
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (o instanceof Argument) {
            Argument other = (Argument) o;
            return new EqualsBuilder()
                .append(name, other.name)
                .append(type, other.type)
                .isEquals();
        } else {
            return false;
        }
    }

    public String getName() {
        return name;
    }

    public Symbol getType() {
        return type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, type);
    }

    @Override
    public String toString() {
        if (type == null) {
            return name;
        } else {
            return name + ":" + type;
        }
    }
}
