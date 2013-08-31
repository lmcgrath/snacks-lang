package snacks.lang.ast;

import static snacks.lang.ast.Type.INTEGER_TYPE;

import java.util.Objects;

public class IntegerConstant extends AstNode {

    private final int value;

    public IntegerConstant(int value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        return o == this || o instanceof IntegerConstant && value == ((IntegerConstant) o).value;
    }

    @Override
    public void print(AstPrinter printer) {
        printer.printIntegerConstant(this);
    }

    @Override
    public void generate(Generator generator) {
        generator.generateIntegerConstant(this);
    }

    @Override
    public Type getType() {
        return INTEGER_TYPE;
    }

    public int getValue() {
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
