package iddic.lang.compiler.syntax;

import java.util.Objects;

public class FunctionArgument extends SyntaxNode {

    private final String id;
    private final QualifiedIdentifier type;

    public FunctionArgument(String id, QualifiedIdentifier type) {
        this.id = id;
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (o instanceof FunctionArgument) {
            FunctionArgument other = (FunctionArgument) o;
            return Objects.equals(id, other.id)
                && Objects.equals(type, other.type);
        } else {
            return false;
        }
    }

    public String getId() {
        return id;
    }

    public QualifiedIdentifier getType() {
        return type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, type);
    }
}
