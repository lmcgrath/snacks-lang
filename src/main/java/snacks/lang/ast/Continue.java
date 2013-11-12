package snacks.lang.ast;

import static snacks.lang.Types.var;

import java.util.Objects;
import snacks.lang.Type;

public class Continue extends AstNode {

    public static final Continue INSTANCE = new Continue();

    private Continue() {
        // intentionally empty
    }

    @Override
    public boolean equals(Object o) {
        return o == this || o instanceof Continue;
    }

    @Override
    public void generate(Generator generator) {
        generator.generateContinue(this);
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
        printer.printContinue(this);
    }

    @Override
    public String toString() {
        return "(Continue)";
    }
}
