package snacks.lang.ast;

import static snacks.lang.Types.voidType;

import java.util.Objects;
import snacks.lang.Type;

public class UnitConstant extends AstNode {

    public static final UnitConstant INSTANCE = new UnitConstant();

    private UnitConstant() {
        // intentionally empty
    }

    @Override
    public boolean equals(Object o) {
        return o == this || o instanceof UnitConstant;
    }

    @Override
    public void generate(Generator generator) {
        generator.generateUnitConstant(this);
    }

    @Override
    public Type getType() {
        return voidType();
    }

    @Override
    public int hashCode() {
        return Objects.hash();
    }

    @Override
    public void print(AstPrinter printer) {
        printer.printUnitConstant(this);
    }

    @Override
    public String toString() {
        return "()";
    }
}
