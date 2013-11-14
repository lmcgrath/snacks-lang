package snacks.lang.ast;

import static snacks.lang.Types.func;
import static snacks.lang.Types.voidType;

import java.util.Objects;
import snacks.lang.Type;

public class VoidFunction extends AstNode {

    private final AstNode body;

    public VoidFunction(AstNode body) {
        this.body = body;
    }

    @Override
    public void print(AstPrinter printer) {
        printer.printVoidFunction(this);
    }

    @Override
    public boolean equals(Object o) {
        return o == this || o instanceof VoidFunction && Objects.equals(body, ((VoidFunction) o).body);
    }

    public AstNode getBody() {
        return body;
    }

    @Override
    public void generate(Generator generator) {
        generator.generateVoidFunction(this);
    }

    @Override
    public Type getType() {
        return func(voidType(), body.getType());
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
        return "(() -> " + body + ")";
    }
}
