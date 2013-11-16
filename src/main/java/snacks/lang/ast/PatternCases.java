package snacks.lang.ast;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import org.apache.commons.lang.builder.EqualsBuilder;
import snacks.lang.Type;

public class PatternCases extends AstNode {

    private final Type type;
    private final List<PatternCase> patterns;

    public PatternCases(Type type, Collection<PatternCase> patterns) {
        this.type = type;
        this.patterns = new ArrayList<>(patterns);
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (o instanceof PatternCases) {
            PatternCases other = (PatternCases) o;
            return new EqualsBuilder()
                .append(type, other.type)
                .append(patterns, other.patterns)
                .isEquals();
        } else {
            return false;
        }
    }

    @Override
    public void generate(Generator generator) {
        generator.generatePatternCases(this);
    }

    public List<PatternCase> getPatterns() {
        return patterns;
    }

    @Override
    public Type getType() {
        return type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, patterns);
    }

    @Override
    public void print(AstPrinter printer) {
        printer.printPatternCases(this);
    }

    @Override
    public String toString() {
        return "(Patterns " + patterns + ")";
    }
}
