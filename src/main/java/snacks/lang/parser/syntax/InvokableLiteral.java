package snacks.lang.parser.syntax;

import java.util.Objects;
import beaver.Symbol;

public class InvokableLiteral extends VisitableSymbol {

    private final Symbol body;

    public InvokableLiteral(Symbol body) {
        this.body = body;
    }

    @Override
    public void accept(SyntaxVisitor visitor) {
        visitor.visitInvokableLiteral(this);
    }

    @Override
    public boolean equals(Object o) {
        return o == this || o instanceof InvokableLiteral && Objects.equals(body, ((InvokableLiteral) o).body);
    }

    public Symbol getBody() {
        return body;
    }

    @Override
    public int hashCode() {
        return Objects.hash(body);
    }

    @Override
    public String toString() {
        return "(-> " + body.toString() + ")";
    }
}
