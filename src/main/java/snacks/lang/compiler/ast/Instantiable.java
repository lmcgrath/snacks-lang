package snacks.lang.compiler.ast;

import static snacks.lang.compiler.ast.Type.VOID_TYPE;
import static snacks.lang.compiler.ast.Type.func;

import java.util.Objects;

public class Instantiable implements AstNode {

    private final AstNode body;

    public Instantiable(AstNode body) {
        this.body = body;
    }

    @Override
    public void accept(AstVisitor visitor) {
        visitor.visitInstantiable(this);
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
