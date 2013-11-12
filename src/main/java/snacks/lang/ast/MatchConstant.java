package snacks.lang.ast;

import java.util.Objects;
import org.apache.commons.lang.builder.EqualsBuilder;
import snacks.lang.Type;

public class MatchConstant extends AstNode {

    private final Reference reference;
    private final AstNode constant;

    public MatchConstant(Reference reference, AstNode constant) {
        this.reference = reference;
        this.constant = constant;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (o instanceof MatchConstant) {
            MatchConstant other = (MatchConstant) o;
            return new EqualsBuilder()
                .append(reference, other.reference)
                .append(constant, other.constant)
                .isEquals();
        } else {
            return false;
        }
    }

    @Override
    public void generate(Generator generator) {
        generator.generateMatchConstant(this);
    }

    public AstNode getConstant() {
        return constant;
    }

    public Reference getReference() {
        return reference;
    }

    @Override
    public Type getType() {
        return constant.getType();
    }

    @Override
    public int hashCode() {
        return Objects.hash(reference, constant);
    }

    @Override
    public void print(AstPrinter printer) {
        printer.printMatchConstant(this);
    }

    @Override
    public String toString() {
        return "(MatchConstant " + reference + " == " + constant + ")";
    }
}
