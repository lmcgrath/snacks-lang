package snacks.lang.ast;

import static snacks.lang.type.Types.func;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import org.apache.commons.lang.builder.EqualsBuilder;
import snacks.lang.type.Type;

public class PatternCase extends AstNode {

    private final ArrayList<AstNode> matchers;
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

    public ArrayList<AstNode> getMatchers() {
        return matchers;
    }

    @Override
    public Type getType() {
        Type type = body.getType();
        for (int i = matchers.size() - 1; i >= 0; i--) {
            type = func(matchers.get(i).getType(), type);
        }
        return type;
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
