package snacks.lang.parser.syntax;

import java.util.Objects;
import snacks.lang.Operator;

public class OperatorDeclaration extends VisitableSymbol {

    private final Operator operator;

    public OperatorDeclaration(Operator operator) {
        this.operator = operator;
    }

    @Override
    public void accept(SyntaxVisitor visitor) {
        visitor.visitOperatorDeclaration(this);
    }

    @Override
    public boolean equals(Object o) {
        return o == this || o instanceof OperatorDeclaration && Objects.equals(operator, ((OperatorDeclaration) o).operator);
    }

    public Operator getOperator() {
        return operator;
    }

    @Override
    public int hashCode() {
        return Objects.hash(operator);
    }

    @Override
    public String toString() {
        return operator.toString();
    }
}
