package snacks.lang.compiler.ast;

import static snacks.lang.compiler.Type.VOID_TYPE;
import static snacks.lang.compiler.Type.func;

import java.util.Objects;
import snacks.lang.SnacksException;
import snacks.lang.compiler.Type;

public class Instantiable implements AstNode {

    private final AstNode body;

    public Instantiable(AstNode body) {
        this.body = body;
    }

    @Override
    public <R, S> R accept(AstVisitor<R, S> visitor, S state) throws SnacksException {
        return visitor.visitInvokable(this, state);
    }

    @Override
    public boolean equals(Object o) {
        return o == this || o instanceof Instantiable && Objects.equals(body, ((Instantiable) o).body);
    }

    public AstNode getBody() {
        return body;
    }

    @Override
    public Type getType() {
        return func(VOID_TYPE, body.getType());
    }

    @Override
    public int hashCode() {
        return Objects.hash(body);
    }

    @Override
    public String toString() {
        return "(" + body + ")()";
    }
}
