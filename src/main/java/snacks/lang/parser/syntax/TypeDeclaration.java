package snacks.lang.parser.syntax;

import java.util.Objects;
import beaver.Symbol;
import org.apache.commons.lang.builder.EqualsBuilder;

public class TypeDeclaration extends Symbol implements Visitable {

    private final String name;
    private final Symbol definition;

    public TypeDeclaration(String name, Symbol definition) {
        this.name = name;
        this.definition = definition;
    }

    @Override
    public void accept(SyntaxVisitor visitor) {
        visitor.visitTypeDeclaration(this);
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (o instanceof TypeDeclaration) {
            TypeDeclaration other = (TypeDeclaration) o;
            return new EqualsBuilder()
                .append(name, other.name)
                .append(definition, other.definition)
                .isEquals();
        } else {
            return false;
        }
    }

    public Symbol getDefinition() {
        return definition;
    }

    public String getName() {
        return name;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, definition);
    }

    @Override
    public String toString() {
        return "(Type " + name + " " + definition + ")";
    }
}
