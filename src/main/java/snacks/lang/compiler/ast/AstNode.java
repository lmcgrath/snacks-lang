package snacks.lang.compiler.ast;

public interface AstNode {

    void accept(AstVisitor visitor);

    Type getType();
}
