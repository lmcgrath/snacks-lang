package snacks.lang.parser.syntax;

import java.util.Objects;

public class CaptureMatcher extends VisitableSymbol {

    private final String variable;

    public CaptureMatcher(String variable) {
        this.variable = variable;
    }

    @Override
    public void accept(SyntaxVisitor visitor) {
        visitor.visitCaptureMatcher(this);
    }

    @Override
    public boolean equals(Object o) {
        return o == this || o instanceof CaptureMatcher && Objects.equals(variable, ((CaptureMatcher) o).variable);
    }

    public String getVariable() {
        return variable;
    }

    @Override
    public int hashCode() {
        return Objects.hash(variable);
    }

    @Override
    public String toString() {
        return "(CaptureMatcher " + variable + ")";
    }
}
