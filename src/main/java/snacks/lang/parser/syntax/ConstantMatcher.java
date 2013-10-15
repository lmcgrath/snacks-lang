package snacks.lang.parser.syntax;

import java.util.Objects;
import beaver.Symbol;

public class ConstantMatcher extends VisitableSymbol {

    private final Symbol constant;

    public ConstantMatcher(Symbol constant) {
        this.constant = constant;
    }

    @Override
    public void accept(SyntaxVisitor visitor) {
        visitor.visitConstantMatcher(this);
    }

    @Override
    public boolean equals(Object o) {
        return o == this || o instanceof ConstantMatcher && Objects.equals(constant, ((ConstantMatcher) o).constant);
    }

    public Symbol getConstant() {
        return constant;
    }

    @Override
    public int hashCode() {
        return Objects.hash(constant);
    }

    @Override
    public String toString() {
        return "(ConstantMatcher " + constant + ")";
    }
}
