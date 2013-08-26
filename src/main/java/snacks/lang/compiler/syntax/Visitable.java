package snacks.lang.compiler.syntax;

import snacks.lang.SnacksException;

public interface Visitable {

    void accept(SyntaxVisitor visitor) throws SnacksException;
}
