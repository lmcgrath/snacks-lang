package snacks.lang.compiler.ast;

import static org.apache.commons.lang.StringUtils.join;

import java.util.List;
import java.util.Objects;

public class Sequence implements AstNode {

    private final List<AstNode> elements;

    public Sequence(List<AstNode> elements) {
        this.elements = elements;
    }

    @Override
    public void accept(AstVisitor visitor) {
        visitor.visitSequence(this);
    }

    @Override
    public boolean equals(Object o) {
        return o == this || o instanceof Sequence && Objects.equals(elements, ((Sequence) o).elements);
    }

    public List<AstNode> getElements() {
        return elements;
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
