package snacks.lang.ast;

import static snacks.lang.Types.var;

import java.util.Objects;
import snacks.lang.Type;

public class Hurl extends AstNode {

    private final AstNode body;

    public Hurl(AstNode body) {
        this.body = body;
    }

    @Override
    public void generate(Generator generator) {
        generator.generateHurl(this);
    }

    @Override
    public boolean equals(Object o) {
        return o == this || o instanceof Hurl && Objects.equals(body, ((Hurl) o).body);
    }

    public AstNode getBody() {
        return body;
    }

    @Override
    public Type getType() {
        return var("T");
    }

    @Override
    public int hashCode() {
        return Objects.hash(body);
    }

    @Override
    public void print(AstPrinter printer) {
        printer.printHurl(this);
    }

    @Override
    public String toString() {
        return "(hurl " + body + ")";
    }
}
