package snacks.lang.parser.syntax;

import java.util.Objects;
import beaver.Symbol;
import org.apache.commons.lang.builder.EqualsBuilder;

public class Loop extends Symbol implements Visitable {

    private final Symbol loopCase;
    private final Symbol defaultCase;

    public Loop(Symbol loopCase, Symbol defaultCase) {
        this.loopCase = loopCase;
        this.defaultCase = defaultCase;
    }

    @Override
    public void accept(SyntaxVisitor visitor) {
        visitor.visitLoop(this);
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (o instanceof Loop) {
            Loop other = (Loop) o;
            return new EqualsBuilder()
                .append(loopCase, other.loopCase)
                .append(defaultCase, other.defaultCase)
                .isEquals();
        } else {
            return false;
        }
    }

    public Symbol getDefaultCase() {
        return defaultCase;
    }

    public Symbol getLoopCase() {
        return loopCase;
    }

    @Override
    public int hashCode() {
        return Objects.hash(loopCase, defaultCase);
    }

    @Override
    public String toString() {
        return "(" + loopCase + " " + defaultCase + ")";
    }
}
