package snacks.lang.compiler.ast;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import org.apache.commons.lang.builder.EqualsBuilder;

public class Closure implements AstNode {

    private final String variable;
    private final List<String> environment;
    private final AstNode body;
    private final Type type;

    public Closure(String variable, Collection<String> environment, AstNode body, Type type) {
        this.variable = variable;
        this.environment = new ArrayList<>(environment);
        this.body = body;
        this.type = type;
    }

    @Override
    public void accept(AstVisitor visitor) {
        visitor.visitClosure(this);
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (o instanceof Closure) {
            Closure other = (Closure) o;
            return new EqualsBuilder()
                .append(variable, other.variable)
                .append(environment, other.environment)
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

    public List<String> getEnvironment() {
        return environment;
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
