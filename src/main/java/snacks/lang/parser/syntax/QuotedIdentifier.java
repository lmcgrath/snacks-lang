package snacks.lang.parser.syntax;

import java.util.Objects;
import beaver.Symbol;

public class QuotedIdentifier extends Symbol implements Visitable {

    private final String name;

    public QuotedIdentifier(String name) {
        this.name = name;
    }

    @Override
    public void accept(SyntaxVisitor visitor) {
        visitor.visitQuotedIdentifier(this);
    }

    @Override
    public boolean equals(Object o) {
        return o == this || o instanceof QuotedIdentifier && Objects.equals(name, ((QuotedIdentifier) o).name);
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
        return "`" + name +  "`";
    }
}
