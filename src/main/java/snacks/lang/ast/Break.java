package snacks.lang.ast;

import static snacks.lang.type.Types.var;

import java.util.Objects;
import snacks.lang.type.Type;

public class Break extends AstNode {

    public static final Break INSTANCE = new Break();

    private Break() {
        // intentionally empty
    }

    @Override
    public boolean equals(Object o) {
        return o == this || o instanceof Break;
    }

    @Override
    public void generate(Generator generator) {
        generator.generateBreak(this);
    }

    @Override
    public Type getType() {
        return var("B");
    }

    @Override
    public int hashCode() {
        return Objects.hash();
    }

    @Override
    public void print(AstPrinter printer) {
        printer.printBreak(this);
    }

    @Override
    public String toString() {
        return "(Break)";
    }
}
