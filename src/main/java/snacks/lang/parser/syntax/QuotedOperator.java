package snacks.lang.parser.syntax;

import java.util.Objects;

public class QuotedOperator extends VisitableSymbol {

    private final String name;

    public QuotedOperator(String name) {
        this.name = name;
    }

    @Override
    public void accept(SyntaxVisitor visitor) {
        visitor.visitQuotedOperator(this);
    }

    @Override
    public boolean equals(Object o) {
        return o == this || o instanceof QuotedOperator && Objects.equals(name, ((QuotedOperator) o).name);
    }

    public String getName() {
        return name;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return "(QuotedOperator " + name + ")";
    }
}
