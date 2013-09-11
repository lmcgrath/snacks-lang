package snacks.lang.parser.syntax;

import static java.util.Arrays.asList;

import java.util.List;
import java.util.Objects;
import beaver.Symbol;
import org.apache.commons.lang.builder.EqualsBuilder;

public class InitializerExpression extends Symbol implements Visitable {

    private final String constructor;
    private final List<Symbol> properties;

    public InitializerExpression(String constructor, Symbol... properties) {
        this.constructor = constructor;
        this.properties = asList(properties);
    }

    @Override
    public void accept(SyntaxVisitor visitor) {
        visitor.visitInitializerExpression(this);
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (o instanceof InitializerExpression) {
            InitializerExpression other = (InitializerExpression) o;
            return new EqualsBuilder()
                .append(constructor, other.constructor)
                .append(properties, other.properties)
                .isEquals();
        } else {
            return false;
        }
    }

    public List<Symbol> getProperties() {
        return properties;
    }

    public String getConstructor() {
        return constructor;
    }

    @Override
    public int hashCode() {
        return Objects.hash(constructor, properties);
    }

    @Override
    public String toString() {
        return "(new " + constructor + " " + properties + ")";
    }
}
