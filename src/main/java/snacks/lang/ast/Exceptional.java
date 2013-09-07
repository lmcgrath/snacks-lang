package snacks.lang.ast;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import org.apache.commons.lang.builder.EqualsBuilder;
import snacks.lang.Type;

public class Exceptional extends AstNode {

    private final AstNode begin;
    private final List<AstNode> embraces;
    private final AstNode ensure;

    public Exceptional(AstNode begin, Collection<AstNode> embraces, AstNode ensure) {
        this.begin = begin;
        this.embraces = new ArrayList<>(embraces);
        this.ensure = ensure;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (o instanceof Exceptional) {
            Exceptional other = (Exceptional) o;
            return new EqualsBuilder()
                .append(begin, other.begin)
                .append(embraces, other.embraces)
                .append(ensure, other.ensure)
                .isEquals();
        } else {
            return false;
        }
    }

    @Override
    public void generate(Generator generator) {
        generator.generateExceptional(this);
    }

    public AstNode getBegin() {
        return begin;
    }

    public List<AstNode> getEmbraces() {
        return embraces;
    }

    public AstNode getEnsure() {
        return ensure;
    }

    @Override
    public Type getType() {
        return begin.getType();
    }

    @Override
    public int hashCode() {
        return Objects.hash(begin, embraces, ensure);
    }

    @Override
    public void print(AstPrinter printer) {
        printer.printExceptional(this);
    }

    @Override
    public String toString() {
        return "(exceptional " + begin + " " + embraces + " " + ensure + ")";
    }
}
