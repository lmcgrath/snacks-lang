package snacks.lang.compiler.ast;

import java.util.Objects;
import org.apache.commons.lang.builder.EqualsBuilder;

public class Reference implements AstNode {

    private final String module;
    private final String name;
    private final Type type;

    public Reference(String module, String name, Type type) {
        this.module = module;
        this.name = name;
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (o instanceof Reference) {
            Reference other = (Reference) o;
            return new EqualsBuilder()
                .append(module, other.module)
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

    @Override
    public Type getType() {
        return type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(module, name, type);
    }

    @Override
    public String toString() {
        return "(" + module + "#" + name + ":" + type + ")";
    }
}
