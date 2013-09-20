package snacks.lang.ast;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import org.apache.commons.lang.builder.EqualsBuilder;
import snacks.lang.RecordType;

public class Initializer extends AstNode {

    private final AstNode constructor;
    private final Map<String, PropertyInitializer> properties;

    public Initializer(AstNode constructor, Map<String, PropertyInitializer> properties) {
        this.constructor = constructor;
        this.properties = new HashMap<>(properties);
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (o instanceof Initializer) {
            Initializer other = (Initializer) o;
            return new EqualsBuilder()
                .append(constructor, other.constructor)
                .append(properties, other.properties)
                .isEquals();
        } else {
            return false;
        }
    }

    @Override
    public void generate(Generator generator) {
        generator.generateInitializer(this);
    }

    public Map<String, PropertyInitializer> getProperties() {
        return properties;
    }

    @Override
    public RecordType getType() {
        return (RecordType) constructor.getType();
    }

    @Override
    public int hashCode() {
        return Objects.hash(constructor, properties);
    }
}
