package snacks.lang.compiler.syntax;

import static java.util.Arrays.asList;
import static org.apache.commons.lang.StringUtils.join;

import java.util.List;
import java.util.Objects;
import beaver.Symbol;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;

public class QualifiedIdentifier extends Symbol {

    private final List<String> segments;

    public QualifiedIdentifier(String... segments) {
        this.segments = ImmutableList.copyOf(segments);
    }

    public QualifiedIdentifier(String segment) {
        segments = asList(segment);
    }

    public QualifiedIdentifier(QualifiedIdentifier head, String segment) {
        segments = new Builder<String>()
            .addAll(head.segments)
            .add(segment)
            .build();
    }

    @Override
    public boolean equals(Object o) {
        return o == this
            || o instanceof QualifiedIdentifier
            && Objects.equals(segments, ((QualifiedIdentifier) o).segments);
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
