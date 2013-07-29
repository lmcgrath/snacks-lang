package iddic.lang.compiler.syntax;

import static java.util.Arrays.asList;
import static iddic.lang.util.StringUtil.stringify;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import iddic.lang.IddicException;

public class FunctionArguments extends SyntaxNode {

    private final List<String> arguments;

    public FunctionArguments(String... args) {
        arguments = asList(args);
    }

    public FunctionArguments(Collection<String> args) {
        arguments = new ArrayList<>(args);
    }

    @Override
    public <R, S> R accept(SyntaxVisitor<R, S> visitor, S state) throws IddicException {
        return visitor.visitFunctionArguments(this, state);
    }

    @Override
    public boolean equals(Object o) {
        return o == this || o instanceof FunctionArguments && Objects.equals(arguments, ((FunctionArguments) o).arguments);
    }

    public List<String> getArguments() {
        return new ArrayList<>(arguments);
    }

    @Override
    public int hashCode() {
        return Objects.hash(arguments);
    }

    @Override
    public String toString() {
        return stringify(this, arguments);
    }
}
