package snacks.lang.ast;

import java.util.Objects;
import org.apache.commons.lang.builder.EqualsBuilder;
import snacks.lang.type.Type;

public class Access extends AstNode {

    private final AstNode expression;
    private final String property;
    private final Type type;

    public Access(AstNode expression, String property, Type type) {
        this.expression = expression;
        this.property = property;
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (o instanceof Access) {
            Access other = (Access) o;
            return new EqualsBuilder()
                .append(expression, other.expression)
                .append(property, other.property)
                .append(type, other.type)
                .isEquals();
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(expression, property, type);
    }

    @Override
    public void print(AstPrinter printer) {
        printer.printAccess(this);
    }

    @Override
    public void generate(Generator generator) {
        generator.generateAccess(this);
    }

    @Override
    public Type getType() {
        return type;
    }

    public AstNode getExpression() {
        return expression;
    }

    public String getProperty() {
        return property;
    }

    @Override
    public String toString() {
        return "(Access " + expression + "." + property + ")";
    }
}
