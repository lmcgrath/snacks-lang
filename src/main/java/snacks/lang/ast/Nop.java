package snacks.lang.ast;

import static snacks.lang.type.Types.var;

import java.util.Objects;
import snacks.lang.type.Type;

public class Nop extends AstNode {

    public static final Nop INSTANCE = new Nop();

    private Nop() {
        // intentionally empty
    }

    @Override
    public boolean equals(Object o) {
        return o == this || o instanceof Nop;
    }

    @Override
    public void generate(Generator generator) {
        generator.generateNop(this);
    }

    @Override
    public Type getType() {
        return var("T");
    }

    @Override
    public int hashCode() {
        return Objects.hash();
    }

    @Override
    public void print(AstPrinter printer) {
        printer.printNop(this);
    }

    @Override
    public String toString() {
        return "(Nop)";
    }
}
