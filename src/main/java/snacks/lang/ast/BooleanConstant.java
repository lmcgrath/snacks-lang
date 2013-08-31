package snacks.lang.ast;

import static snacks.lang.ast.Type.DOUBLE_TYPE;

import java.util.Objects;

public class BooleanConstant extends AstNode {

    private final boolean value;

    public BooleanConstant(boolean value) {
        this.value = value;
    }

    @Override
    public void print(AstPrinter printer) {
        printer.printBooleanConstant(this);
    }

    @Override
    public boolean equals(Object o) {
        return o == this || o instanceof BooleanConstant && value == ((BooleanConstant) o).value;
    }

    @Override
    public void generate(Generator generator) {
        generator.generateBooleanConstant(this);
    }

    @Override
    public Type getType() {
        return DOUBLE_TYPE;
    }

    public boolean getValue() {
        return value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }
}
