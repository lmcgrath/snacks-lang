package snacks.lang.ast;

import static snacks.lang.type.Types.VOID_TYPE;
import static snacks.lang.type.Types.func;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import org.apache.commons.lang.builder.EqualsBuilder;
import snacks.lang.type.Type;

public class Closure extends AstNode {

    private final List<String> environment;
    private final AstNode body;

    public Closure(Collection<String> environment, AstNode body) {
        this.environment = new ArrayList<>(environment);
        this.body = body;
    }

    @Override
    public void print(AstPrinter printer) {
        printer.printClosure(this);
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (o instanceof Closure) {
            Closure other = (Closure) o;
            return new EqualsBuilder()
                .append(environment, other.environment)
                .append(body, other.body)
                .isEquals();
        } else {
            return false;
        }
    }

    public AstNode getBody() {
        return body;
    }

    public List<String> getEnvironment() {
        return environment;
    }

    @Override
    public void generate(Generator generator) {
        generator.generateClosure(this);
    }

    @Override
    public Type getType() {
        return func(VOID_TYPE, body.getType());
    }

    @Override
    public boolean isInvokable() {
        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hash(body);
    }

    @Override
    public String toString() {
        return "(-> " + body + ")";
    }
}
