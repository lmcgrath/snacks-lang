package iddic.lang.compiler.syntax;

import static java.util.Arrays.asList;
import static org.apache.commons.lang.StringUtils.join;
import static iddic.lang.util.StringUtil.stringify;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import iddic.lang.IddicException;

public class RootIdentifier extends SyntaxNode {

    private final List<String> segments;

    public RootIdentifier(String[] segments) {
        this.segments = asList(segments);
    }

    public RootIdentifier(Collection<String> segments) {
        this.segments = new ArrayList<>(segments);
    }

    @Override
    public <R, S> R accept(SyntaxVisitor<R, S> visitor, S state) throws IddicException {
        return visitor.visitRootIdentifier(this, state);
    }

    @Override
    public boolean equals(Object o) {
        return o == this || o instanceof RootIdentifier && Objects.equals(segments, ((RootIdentifier) o).segments);
    }

    public List<String> getSegments() {
        return new ArrayList<>(segments);
    }

    @Override
    public int hashCode() {
        return Objects.hash(segments);
    }

    @Override
    public String toString() {
        return stringify(this, join(segments, '.'));
    }
}
