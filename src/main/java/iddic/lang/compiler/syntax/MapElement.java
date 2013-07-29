package iddic.lang.compiler.syntax;

import static iddic.lang.util.StringUtil.stringify;

import java.util.Objects;

public class MapElement extends SyntaxNode {

    private final SyntaxNode key;
    private final SyntaxNode value;

    public MapElement(SyntaxNode key, SyntaxNode value) {
        this.key = key;
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (o instanceof MapElement) {
            MapElement other = (MapElement) o;
            return Objects.equals(key, other.key)
                && Objects.equals(value, other.value);
        } else {
            return false;
        }
    }

    public SyntaxNode getKey() {
        return key;
    }

    public SyntaxNode getValue() {
        return value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(key, value);
    }

    @Override
    public String toString() {
        return stringify(this, key, value);
    }
}
