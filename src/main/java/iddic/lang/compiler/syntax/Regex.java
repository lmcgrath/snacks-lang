package iddic.lang.compiler.syntax;

import static iddic.lang.util.StringUtil.stringify;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class Regex extends SyntaxNode {

    private final SyntaxNode expression;
    private final Set<String> options;

    public Regex(SyntaxNode expression, Set<String> options) {
        this.expression = expression;
        this.options = options;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (o instanceof Regex) {
            Regex other = (Regex) o;
            return Objects.equals(expression, other.expression)
                && Objects.equals(options, other.options);
        } else {
            return false;
        }
    }

    public SyntaxNode getExpression() {
        return expression;
    }

    public Set<String> getOptions() {
        return new HashSet<>(options);
    }

    @Override
    public int hashCode() {
        return Objects.hash(expression, options);
    }

    @Override
    public String toString() {
        return stringify(this, expression, options);
    }
}
