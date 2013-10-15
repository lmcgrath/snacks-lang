package snacks.lang.ast;

import static snacks.lang.type.Types.tuple;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import snacks.lang.type.Type;

public class TupleInitializer extends AstNode {

    private final List<AstNode> elements;

    public TupleInitializer(List<AstNode> elements) {
        this.elements = elements;
    }

    @Override
    public boolean equals(Object o) {
        return o == this || o instanceof TupleInitializer && Objects.equals(elements, ((TupleInitializer) o).elements);
    }

    @Override
    public void generate(Generator generator) {
        generator.generateTupleInitializer(this);
    }

    public List<AstNode> getElements() {
        return elements;
    }

    @Override
    public Type getType() {
        List<Type> types = new ArrayList<>();
        for (AstNode element : elements) {
            types.add(element.getType());
        }
        return tuple(types);
    }

    @Override
    public int hashCode() {
        return Objects.hash(elements);
    }

    @Override
    public void print(AstPrinter printer) {
        printer.printTupleInitializer(this);
    }

    @Override
    public String toString() {
        return "(TupleInitializer " + elements + ")";
    }
}
