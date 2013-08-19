package snacks.lang.compiler;

import java.util.List;
import com.google.common.collect.ImmutableList;

public class TypePossibility implements Type {

    private final List<Type> possibilities;

    public TypePossibility(List<Type> possibilities) {
        this.possibilities = ImmutableList.copyOf(possibilities);
    }

    @Override
    public void bind(Type type) {
        throw new IllegalStateException("Cannot bind to non-generic type");
    }

    @Override
    public Type expose() {
        return this;
    }

    @Override
    public List<Type> getPossibilities() {
        return possibilities;
    }

    @Override
    public String getName() {
        return "?";
    }

    @Override
    public List<Type> getParameters() {
        return ImmutableList.of();
    }

    @Override
    public boolean isFunction() {
        for (Type type : possibilities) {
            if (type.isFunction()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isParameterized() {
        for (Type type : possibilities) {
            if (type.isParameterized()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isPossibility() {
        return true;
    }

    @Override
    public boolean isVariable() {
        for (Type type : possibilities) {
            if (type.isVariable()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return "?" + possibilities;
    }
}
