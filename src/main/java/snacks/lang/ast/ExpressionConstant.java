package snacks.lang.ast;

import java.util.Objects;

public class ExpressionConstant extends AstNode {

    private final AstNode value;

    public ExpressionConstant(AstNode value) {
        this.value = value;
    }

    @Override
    public void print(AstPrinter printer) {
        printer.printExpressionConstant(this);
    }

    @Override
    public boolean equals(Object o) {
        return o == this || o instanceof ExpressionConstant && Objects.equals(value, ((ExpressionConstant) o).value);
    }

    @Override
    public void generate(Generator generator) {
        generator.generateExpressionConstant(this);
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
        return "(constant " + value + ")";
    }
}
