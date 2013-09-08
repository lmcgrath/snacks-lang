package snacks.lang.parser.syntax;

import static org.apache.commons.lang.StringUtils.join;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import beaver.Symbol;

public class TupleSignature extends Symbol implements Visitable {

    private final List<Symbol> types;

    public TupleSignature(Collection<Symbol> types) {
        this.types = new ArrayList<>(types);
    }

    @Override
    public void accept(SyntaxVisitor visitor) {
        visitor.visitTupleSignature(this);
    }

    @Override
    public boolean equals(Object o) {
        return o == this || o instanceof TupleSignature && Objects.equals(types, ((TupleSignature) o).types);
    }

    public List<Symbol> getTypes() {
        return types;
    }

    @Override
    public int hashCode() {
        return Objects.hash(types);
    }

    @Override
    public String toString() {
        return "(" + join(types, ", ") + ")";
    }
}
