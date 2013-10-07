package snacks.lang.ast;

import java.util.List;
import java.util.Objects;
import org.apache.commons.lang.builder.EqualsBuilder;
import snacks.lang.type.Type;

public class Initializer extends AstNode {

    private final AstNode constructor;
    private final List<AstNode> arguments;

    public Initializer(AstNode constructor, List<AstNode> arguments) {
        this.constructor = constructor;
        this.arguments = arguments;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (o instanceof Initializer) {
            Initializer other = (Initializer) o;
            return new EqualsBuilder()
                .append(constructor, other.constructor)
                .append(arguments, other.arguments)
                .isEquals();
        } else {
            return false;
        }
    }

    @Override
    public void generate(Generator generator) {
        generator.generateInitializer(this);
    }

    public List<AstNode> getArguments() {
        return arguments;
    }

    public AstNode getConstructor() {
        return constructor;
    }

    @Override
    public Type getType() {
        return constructor.getType();
    }

    @Override
    public int hashCode() {
        return Objects.hash(constructor, arguments);
    }

    @Override
    public String toString() {
        return "(Initializer " + constructor + " " + arguments + ")";
    }
}
