package snacks.lang.ast;

import static snacks.lang.Type.type;

import java.util.Objects;
import org.apache.commons.lang.builder.EqualsBuilder;
import snacks.lang.Type;

public class PropertyInitializer extends AstNode {

    private final String name;
    private final AstNode value;

    public PropertyInitializer(String name, AstNode value) {
        this.name = name;
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (o instanceof PropertyInitializer) {
            PropertyInitializer other = (PropertyInitializer) o;
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
        return type(name, value.getType());
    }

    public AstNode getValue() {
        return value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, value);
    }
}
