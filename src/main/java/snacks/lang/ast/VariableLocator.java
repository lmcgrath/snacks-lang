package snacks.lang.ast;

import java.util.Objects;

public class VariableLocator extends Locator {

    private final String name;

    public VariableLocator(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        return o == this || o instanceof VariableLocator && Objects.equals(name, ((VariableLocator) o).name);
    }

    @Override
    public void generate(Generator generator) {
        generator.generateVariableLocator(this);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public boolean isVariable() {
        return true;
    }

    @Override
    public void print(AstPrinter printer) {
        printer.printVariableLocator(this);
    }

    @Override
    public void reduce(Reducer reducer) {
        reducer.reduceVariableLocator(this);
    }

    @Override
    public String toString() {
        return name;
    }
}
