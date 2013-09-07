package snacks.lang.ast;

public abstract class Locator {

    public abstract void accept(LocatorVisitor visitor);

    public void generate(Generator generator) {
        throw new UnsupportedOperationException("Cannot reference " + getClass().getSimpleName());
    }

    public abstract String getName();

    public boolean isVariable() {
        return false;
    }

    public abstract void print(AstPrinter printer);

    public void reduce(Reducer reducer) {
        throw new UnsupportedOperationException("Cannot assign to " + getClass().getSimpleName());
    }
}
