package snacks.lang.ast;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import org.apache.commons.lang.builder.EqualsBuilder;
import snacks.lang.Type;

public class PatternCase extends AstNode {

    private final List<AstNode> matchers;
    private final AstNode body;

    public PatternCase(Collection<AstNode> matchers, AstNode body) {
        this.matchers = new ArrayList<>(matchers);
        this.body = body;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (o instanceof PatternCase) {
            PatternCase other = (PatternCase) o;
            return new EqualsBuilder()
                .append(matchers, other.matchers)
                .append(body, other.body)
                .isEquals();
        } else {
            return false;
        }
    }

    @Override
    public void generate(Generator generator) {
        generator.generatePatternCase(this);
    }

    public AstNode getBody() {
        return body;
    }

    public List<Type> getMatcherTypes() {
        List<Type> matcherTypes = new ArrayList<>();
        for (AstNode matcher : matchers) {
            matcherTypes.add(matcher.getType());
        }
        return matcherTypes;
    }

    public List<AstNode> getMatchers() {
        return matchers;
    }

    @Override
    public Type getType() {
        return body.getType();
    }

    @Override
    public int hashCode() {
        return Objects.hash(matchers, body);
    }

    @Override
    public void print(AstPrinter printer) {
        printer.printPatternCase(this);
    }

    @Override
    public String toString() {
        return "(PatternCase " + matchers + " -> " + body + ")";
    }
}
