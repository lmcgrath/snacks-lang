package snacks.lang.ast;

import static org.apache.commons.lang.StringUtils.join;

import java.util.List;
import java.util.Objects;

public class Sequence extends AstNode {

    private final List<AstNode> elements;

    public Sequence(List<AstNode> elements) {
        this.elements = elements;
    }

    @Override
    public void print(AstPrinter printer) {
        printer.printSequence(this);
    }

    @Override
    public boolean equals(Object o) {
        return o == this || o instanceof Sequence && Objects.equals(elements, ((Sequence) o).elements);
    }

    public List<AstNode> getElements() {
        return elements;
    }

    @Override
    public void generate(Generator generator) {
        generator.generateSequence(this);
    }

    @Override
    public Type getType() {
        return elements.get(elements.size() - 1).getType();
    }

    @Override
    public int hashCode() {
        return Objects.hash(elements);
    }

    @Override
    public String toString() {
        return "{ " + join(elements, "; ") + " }";
    }
}
