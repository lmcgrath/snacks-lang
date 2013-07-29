package iddic.lang.compiler.syntax;

import java.net.URL;
import iddic.lang.IddicException;

public abstract class SyntaxNode {

    private final URL source;

    public SyntaxNode() {
        source = null;
    }

    public SyntaxNode(URL source) {
        this.source = source;
    }

    public <R, S> R accept(SyntaxVisitor<R, S> visitor, S state) throws IddicException {
        throw new UnsupportedOperationException("Cannot visit " + getClass().getSimpleName() + " node");
    }

    public URL getSource() {
        return source;
    }
}
