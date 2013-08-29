package snacks.lang.compiler.ast;

import java.util.Objects;
import org.apache.commons.lang.builder.EqualsBuilder;

public class VariableDeclaration implements AstNode {

    private final String name;
    private final AstNode value;

    public VariableDeclaration(String name, AstNode value) {
        this.name = name;
        this.value = value;
    }

    @Override
    public void accept(AstVisitor visitor) {
        visitor.visitVariableDeclaration(this);
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (o instanceof VariableDeclaration) {
            VariableDeclaration other = (VariableDeclaration) o;
            return new EqualsBuilder()
                .append(name, other.name)
                .append(value, other.value)
                .isEquals();
        } else {
            return false;
        }
    }

    public String getName() {
        return name;
    }

    @Override
    public Type getType() {
        return value.getType();
    }

    @Override
    public boolean isInvokable() {
        return false;
    }

    public AstNode getValue() {
        return value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, value);
    }

    @Override
    public String toString() {
        return "(var " + name + " = " + value + ")";
    }
}
