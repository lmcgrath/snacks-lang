package snacks.lang.ast;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import org.apache.commons.lang.builder.EqualsBuilder;
import snacks.lang.type.Type;

public class MatchConstructor extends AstNode {

    private final Reference reference;
    private final List<AstNode> parameters;

    public MatchConstructor(Reference reference, Collection<AstNode> parameters) {
        this.reference = reference;
        this.parameters = new ArrayList<>(parameters);
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (o instanceof MatchConstructor) {
            MatchConstructor other = (MatchConstructor) o;
            return new EqualsBuilder()
                .append(reference, other.reference)
                .append(parameters, other.parameters)
                .isEquals();
        } else {
            return false;
        }
    }

    @Override
    public void generate(Generator generator) {
        generator.generateMatchConstructor(this);
    }

    public Reference getReference() {
        return reference;
    }

    public List<AstNode> getParameters() {
        return parameters;
    }

    @Override
    public Type getType() {
        return reference.getType();
    }

    @Override
    public int hashCode() {
        return Objects.hash(reference, parameters);
    }

    @Override
    public void print(AstPrinter printer) {
        printer.printMatchConstructor(this);
    }

    @Override
    public String toString() {
        return "(MatchConstructor " + reference + " " + parameters + ")";
    }
}
