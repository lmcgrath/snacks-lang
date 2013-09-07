package snacks.lang.ast;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import org.apache.commons.lang.builder.EqualsBuilder;

public class ClosureLocator extends Locator {

    private final String module;
    private final String name;
    private final List<String> environment;

    public ClosureLocator(String module, String name, Collection<String> environment) {
        this.module = module;
        this.name = name;
        this.environment = new ArrayList<>(environment);
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

    @Override
    public void accept(LocatorVisitor visitor) {
        visitor.visitClosureLocator(this);
    }

    @Override
    public void generate(Generator generator) {
        generator.generateClosureLocator(this);
    }

    public List<String> getEnvironment() {
        return environment;
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
    public void print(AstPrinter printer) {
        printer.printClosureLocator(this);
    }

    @Override
    public String toString() {
        return module + "#" + name;
    }
}
