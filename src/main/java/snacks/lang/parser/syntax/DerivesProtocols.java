package snacks.lang.parser.syntax;

import java.util.List;
import java.util.Objects;
import beaver.Symbol;
import com.google.common.collect.ImmutableList;

public class DerivesProtocols extends VisitableSymbol {

    private final List<Symbol> protocols;

    public DerivesProtocols(List<Symbol> protocols) {
        this.protocols = ImmutableList.copyOf(protocols);
    }

    @Override
    public void accept(SyntaxVisitor visitor) {
        throw new UnsupportedOperationException(); // TODO
    }

    @Override
    public boolean equals(Object o) {
        return o == this || o instanceof DerivesProtocols && Objects.equals(protocols, ((DerivesProtocols) o).protocols);
    }

    @Override
    public int hashCode() {
        return Objects.hash(protocols);
    }

    @Override
    public String toString() {
        return "(Implements " + protocols + ")";
    }
}
