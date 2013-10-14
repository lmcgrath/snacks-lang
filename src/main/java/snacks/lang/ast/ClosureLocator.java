package snacks.lang.ast;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

public class ClosureLocator extends Locator {

    private final String qualifiedName;
    private final List<String> environment;

    public ClosureLocator(String qualifiedName, Collection<String> environment) {
        this.qualifiedName = qualifiedName;
        this.environment = new ArrayList<>(environment);
    }

    @Override
    public boolean equals(Object o) {
        return o == this || o instanceof ClosureLocator && Objects.equals(qualifiedName, ((ClosureLocator) o).qualifiedName);
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

    @Override
    public String getName() {
        return qualifiedName;
    }

    @Override
    public int hashCode() {
        return Objects.hash(qualifiedName);
    }

    @Override
    public void print(AstPrinter printer) {
        printer.printClosureLocator(this);
    }

    @Override
    public String toString() {
        return qualifiedName;
    }
}
