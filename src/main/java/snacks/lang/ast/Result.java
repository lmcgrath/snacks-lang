package snacks.lang.ast;

import java.util.Objects;
import snacks.lang.Type;

public class Result extends AstNode {

    private final AstNode value;

    public Result(AstNode value) {
        this.value = value;
    }

    @Override
    public void print(AstPrinter printer) {
        printer.printResult(this);
    }

    @Override
    public boolean equals(Object o) {
        return o == this || o instanceof Result && Objects.equals(value, ((Result) o).value);
    }

    @Override
    public void generate(Generator generator) {
        generator.generateResult(this);
    }

    @Override
    public Type getType() {
        return value.getType();
    }

    public AstNode getValue() {
        return value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return "(result " + value + ")";
    }
}
