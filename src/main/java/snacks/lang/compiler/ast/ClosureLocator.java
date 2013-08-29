package snacks.lang.compiler.ast;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import org.apache.commons.lang.builder.EqualsBuilder;

public class ClosureLocator implements Locator {

    private final String module;
    private final String name;
    private final List<String> environment;

    public ClosureLocator(String module, String name, Collection<String> environment) {
        this.module = module;
        this.name = name;
        this.environment = new ArrayList<>(environment);
    }

    @Override
    public void accept(AstVisitor visitor) {
        visitor.visitClosureLocator(this);
    }

    public List<String> getEnvironment() {
        return environment;
    }

    @Override
    public boolean isVariable() {
        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (o instanceof ClosureLocator) {
            ClosureLocator other = (ClosureLocator) o;
            return new EqualsBuilder()
                .append(module, other.module)
                .append(name, other.name)
                .isEquals();
        } else {
            return false;
        }
    }

    public String getModule() {
        return module;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int hashCode() {
        return Objects.hash(module, name);
    }

    @Override
    public String toString() {
        return module + "#" + name;
    }
}
