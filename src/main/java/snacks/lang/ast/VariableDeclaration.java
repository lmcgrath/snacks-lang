package snacks.lang.ast;

import static snacks.lang.ast.Type.var;

import java.util.Objects;

public class VariableDeclaration extends AstNode {

    private final String name;

    public VariableDeclaration(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        return o == this || o instanceof VariableDeclaration && Objects.equals(name, ((VariableDeclaration) o).name);
    }

    @Override
    public void generate(Generator generator) {
        generator.generateVariableDeclaration(this);
    }

    public String getName() {
        return name;
    }

    @Override
    public Type getType() {
        return var("X");
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public void print(AstPrinter printer) {
        printer.printVariableDeclaration(this);
    }

    @Override
    public void reduce(Reducer reducer) {
        reducer.reduceVariableDeclaration(this);
    }

    @Override
    public String toString() {
        return "(var " + name + ")";
    }
}
