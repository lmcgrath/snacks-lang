package snacks.lang.compiler.syntax;

import java.util.Objects;
import beaver.Symbol;
import org.apache.commons.lang.builder.EqualsBuilder;

public class Annotation extends Symbol implements Visitable {

    private final Symbol name;
    private final Symbol value;

    public Annotation(Symbol name, Symbol value) {
        this.name = name;
        this.value = value;
    }

    @Override
    public void accept(SyntaxVisitor visitor) {
        visitor.visitAnnotation(this);
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (o instanceof Annotation) {
            Annotation other = (Annotation) o;
            return new EqualsBuilder()
                .append(name, other.name)
                .append(value, other.value)
                .isEquals();
        } else {
            return false;
        }
    }

    public Symbol getName() {
        return name;
    }

    public Symbol getValue() {
        return value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, value);
    }

    @Override
    public String toString() {
        return "@" + name + (value instanceof NothingLiteral ? "" : "(" + value + ")");
    }
}
