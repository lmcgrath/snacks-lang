package snacks.lang.ast;

import java.util.Objects;
import org.apache.commons.lang.builder.EqualsBuilder;
import snacks.lang.type.Type;

public class Embrace extends AstNode {

    private final String exception;
    private final String variable;
    private final AstNode body;

    public Embrace(String variable, String exception, AstNode body) {
        this.exception = exception;
        this.variable = variable;
        this.body = body;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (o instanceof Embrace) {
            Embrace other = (Embrace) o;
            return new EqualsBuilder()
                .append(exception, other.exception)
                .append(body, other.body)
                .isEquals();
        } else {
            return false;
        }
    }

    @Override
    public void generate(Generator generator) {
        generator.generateEmbrace(this);
    }

    public AstNode getBody() {
        return body;
    }

    public String getException() {
        return exception;
    }

    @Override
    public Type getType() {
        return body.getType();
    }

    public String getVariable() {
        return variable;
    }

    @Override
    public int hashCode() {
        return Objects.hash(body);
    }

    @Override
    public void print(AstPrinter printer) {
        printer.printEmbrace(this);
    }

    @Override
    public String toString() {
        return "(" + variable + ":" + exception + " -> " + body + ")";
    }
}
