package snacks.lang.parser.syntax;

import static org.apache.commons.lang.StringUtils.join;

import java.util.List;
import java.util.Objects;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;

public class QualifiedIdentifier extends VisitableSymbol {

    private final List<String> segments;

    public QualifiedIdentifier(String... segments) {
        this.segments = ImmutableList.copyOf(segments);
    }

    public QualifiedIdentifier(QualifiedIdentifier head, String segment) {
        segments = new Builder<String>()
            .addAll(head.segments)
            .add(segment)
            .build();
    }

    @Override
    public void accept(SyntaxVisitor visitor) {
        visitor.visitQualifiedIdentifier(this);
    }

    @Override
    public boolean equals(Object o) {
        return o == this
            || o instanceof QualifiedIdentifier
            && Objects.equals(segments, ((QualifiedIdentifier) o).segments);
    }

    public List<String> getSegments() {
        return segments;
    }

    @Override
    public int hashCode() {
        return Objects.hash(segments);
    }

    public String getLastSegment() {
        return segments.get(segments.size() - 1);
    }

    @Override
    public String toString() {
        return join(segments, '.');
    }
}
