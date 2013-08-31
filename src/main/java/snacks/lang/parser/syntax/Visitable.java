package snacks.lang.parser.syntax;

public interface Visitable {

    void accept(SyntaxVisitor visitor);
}
