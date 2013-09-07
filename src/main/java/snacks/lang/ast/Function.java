package snacks.lang.ast;

import java.util.Objects;
import org.apache.commons.lang.builder.EqualsBuilder;
import snacks.lang.Type;

public class Function extends AstNode {

    private final String variable;
    private final AstNode body;
    private final Type type;

    public Function(String variable, AstNode body, Type type) {
        this.variable = variable;
        this.body = body;
        this.type = type;
    }

    @Override
    public void print(AstPrinter printer) {
        printer.printFunction(this);
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (o instanceof Function) {
            Function other = (Function) o;
            return new EqualsBuilder()
                .append(variable, other.variable)
                .append(body, other.body)
                .append(type, other.type)
                .isEquals();
        } else {
            return false;
        }
    }

    public AstNode getBody() {
        return body;
    }

    @Override
    public void generate(Generator generator) {
        generator.generateFunction(this);
    }

    @Override
    public Type getType() {
        return type;
    }

    @Override
    public boolean isInvokable() {
        return true;
    }

    public String getVariable() {
        return variable;
    }

    @Override
    public int hashCode() {
        return Objects.hash(variable, body, type);
    }

    @Override
    public String toString() {
        return "(" + variable + " -> " + body + "):" + type;
    }
}
