package snacks.lang;

import static snacks.lang.Fixity.RIGHT;

import java.util.Objects;
import org.apache.commons.lang.builder.EqualsBuilder;

public class Operator {

    private final Fixity fixity;
    private final int precedence;
    private final int arity;
    private final boolean shortCircuit;
    private final String name;

    public Operator(Fixity fixity, int precedence, int arity, boolean shortCircuit, String name) {
        this.fixity = fixity;
        this.precedence = precedence;
        this.arity = arity;
        this.shortCircuit = shortCircuit;
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (o instanceof Operator) {
            Operator other = (Operator) o;
            return new EqualsBuilder()
                .append(fixity, other.fixity)
                .append(precedence, other.precedence)
                .append(arity, other.arity)
                .append(shortCircuit, other.shortCircuit)
                .append(name, other.name)
                .isEquals();
        } else {
            return false;
        }
    }

    public Fixity getFixity() {
        return fixity;
    }

    public String getName() {
        return name;
    }

    public int getPrecedence() {
        return precedence;
    }

    @Override
    public int hashCode() {
        return Objects.hash(fixity, precedence, name);
    }

    public boolean isPrefix() {
        return arity == 1 && fixity == RIGHT;
    }

    public boolean isShortCircuit() {
        return shortCircuit;
    }

    public Operator toPrefix(String name) {
        return new Operator(RIGHT, precedence, 1, false, name);
    }

    @Override
    public String toString() {
        if (isPrefix()) {
            return "Prefix(" + name + ", " + precedence + ")";
        } else {
            return "Infix(" + name + " " + fixity + ", " + precedence + ")";
        }
    }
}
