package snacks.lang.ast;

import static snacks.lang.Types.symbolType;

import java.util.Objects;
import snacks.lang.Type;

public class SymbolConstant extends AstNode {

    private final String name;

    public SymbolConstant(String name) {
        this.name = name;
    }

    @Override
    public void generate(Generator generator) {
        generator.generateSymbol(this);
    }

    @Override
    public boolean equals(Object o) {
        return o == this || o instanceof SymbolConstant && Objects.equals(o, ((SymbolConstant) o).name);
    }

    public String getName() {
        return name;
    }

    @Override
    public Type getType() {
        return symbolType();
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public void print(AstPrinter printer) {
        printer.printSymbolConstant(this);
    }

    @Override
    public String toString() {
        return "(symbol :" + name + ")";
    }
}
