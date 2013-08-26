package snacks.lang.compiler.syntax;

public interface Visitable {

    void accept(SyntaxVisitor visitor);
}
