package snacks.lang.compiler.ast;

import java.util.Objects;
import org.apache.commons.lang.builder.EqualsBuilder;
import snacks.lang.SnacksException;
import snacks.lang.compiler.Type;

public class DeclaredExpression implements AstNode {

    private final String module;
    private final String name;
    private final AstNode body;

    public DeclaredExpression(String module, String name, AstNode body) {
        this.module = module;
        this.name = name;
        this.body = body;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (o instanceof DeclaredExpression) {
            DeclaredExpression other = (DeclaredExpression) o;
            return new EqualsBuilder()
                .append(module, other.module)
                .append(name, other.name)
                .append(body, other.body)
                .isEquals();
        } else {
            return false;
        }
    }

    public AstNode getBody() {
        return body;
    }

    public String getName() {
        return name;
    }

    @Override
    public void accept(AstVisitor visitor) throws SnacksException {
        visitor.visitDeclaredExpression(this);
    }

    @Override
    public Type getType() {
        return body.getType();
    }

    @Override
    public int hashCode() {
        return Objects.hash(module, name, body);
    }

    @Override
    public String toString() {
        return "(" + module + "#" + name + " = " + body + ")";
    }
}
