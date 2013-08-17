package snacks.lang.compiler.syntax;

import java.util.Objects;
import beaver.Symbol;
import org.apache.commons.lang.builder.EqualsBuilder;
import snacks.lang.SnacksException;

public class Loop extends Symbol implements Visitable {

    private final Symbol loopCase;
    private final Symbol defaultCase;

    public Loop(Symbol loopCase, Symbol defaultCase) {
        this.loopCase = loopCase;
        this.defaultCase = defaultCase;
    }

    @Override
    public <R, S> R accept(SyntaxVisitor<R, S> visitor, S state) throws SnacksException {
        return visitor.visitLoop(this, state);
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
