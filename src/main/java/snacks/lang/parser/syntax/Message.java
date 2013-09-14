package snacks.lang.parser.syntax;

import static java.util.Arrays.asList;
import static org.apache.commons.lang.StringUtils.join;

import java.util.List;
import java.util.Objects;
import beaver.Symbol;

public class Message extends VisitableSymbol {

    private final List<Symbol> elements;

    public Message(Symbol... elements) {
        this.elements = asList(elements);
    }

    @Override
    public void accept(SyntaxVisitor visitor) {
        visitor.visitMessage(this);
    }

    @Override
    public boolean equals(Object o) {
        return o == this || o instanceof Message && Objects.equals(elements, ((Message) o).elements);
    }

    public List<Symbol> getElements() {
        return elements;
    }

    @Override
    public int hashCode() {
        return Objects.hash(elements);
    }

    @Override
    public String toString() {
        return "(message " + join(elements, ", ") + ")";
    }
}
