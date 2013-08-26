package snacks.lang.compiler.ast;

public interface Locator {

    void accept(AstVisitor visitor);
}
