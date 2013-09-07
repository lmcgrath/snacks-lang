package snacks.lang.ast;

import static snacks.lang.Type.DOUBLE_TYPE;

import java.util.Objects;
import snacks.lang.Type;

public class DoubleConstant extends AstNode {

    private final double value;

    public DoubleConstant(double value) {
        this.value = value;
    }

    @Override
    public void print(AstPrinter printer) {
        printer.printDoubleConstant(this);
    }

    @Override
    public boolean equals(Object o) {
        return o == this || o instanceof DoubleConstant && value == ((DoubleConstant) o).value;
    }

    @Override
    public void generate(Generator generator) {
        generator.generateDoubleConstant(this);
    }

    @Override
    public Type getType() {
        return DOUBLE_TYPE;
    }

    public double getValue() {
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
