package snacks.lang.ast;

import java.util.Objects;
import snacks.lang.Type;

public class Begin extends AstNode {

    private final AstNode body;

    public Begin(AstNode body) {
        this.body = body;
    }

    @Override
    public boolean equals(Object o) {
        return o == this || o instanceof Begin && Objects.equals(body, ((Begin) o).body);
    }

    @Override
    public void generate(Generator generator) {
        generator.generateBegin(this);
    }

    public AstNode getBody() {
        return body;
    }

    @Override
    public Type getType() {
        return body.getType();
    }

    @Override
    public int hashCode() {
        return Objects.hash(body);
    }

    @Override
    public String toString() {
        return "(begin " + body + ")";
    }
}
