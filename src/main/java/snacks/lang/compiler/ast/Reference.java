package snacks.lang.compiler.ast;

import static java.util.Arrays.asList;

import java.util.List;
import java.util.Objects;
import com.google.common.collect.ImmutableList;
import org.apache.commons.lang.builder.EqualsBuilder;
import snacks.lang.SnacksException;
import snacks.lang.compiler.SymbolEnvironment;
import snacks.lang.compiler.Type;

public class Reference implements AstNode {

    private final State state;

    public Reference(Locator locator, Type type) {
        this.state = new BoundState(locator, type);
    }

    public Reference(Locator locator, SymbolEnvironment environment) {
        this.state = new UnboundState(locator, environment.typesOf(locator));
    }

    @Override
    public boolean equals(Object o) {
        return o == this || o instanceof Reference && Objects.equals(state, ((Reference) o).state);
    }

    public Locator getLocator() {
        return state.getLocator();
    }

    public String getModule() {
        return state.getModule();
    }

    public String getName() {
        return state.getName();
    }

    @Override
    public <R, S> R accept(AstVisitor<R, S> visitor, S state) throws SnacksException {
        return visitor.visitReference(this, state);
    }

    @Override
    public Reference getReference() {
        return this;
    }

    @Override
    public Type getType() {
        return state.getType();
    }

    public List<Type> getPossibleTypes() {
        return state.getPossibleTypes();
    }

    @Override
    public boolean isFunction() {
        return state.isFunction();
    }

    @Override
    public boolean isReference() {
        return true;
    }

    @Override
    public boolean hasType() {
        return state.hasType();
    }

    @Override
    public int hashCode() {
        return Objects.hash(state);
    }

    @Override
    public String toString() {
        return state.toString();
    }

    private interface State {

        Locator getLocator();

        String getModule();

        String getName();

        List<Type> getPossibleTypes();

        Type getType();

        boolean hasType();

        boolean isFunction();
    }

    private static final class BoundState implements State {

        private final Locator locator;
        private final Type type;

        public BoundState(Locator locator, Type type) {
            this.locator = locator;
            this.type = type;
        }

        @Override
        public boolean equals(Object o) {
            if (o == this) {
                return true;
            } else if (o instanceof BoundState) {
                BoundState other = (BoundState) o;
                return new EqualsBuilder()
                    .append(locator, other.locator)
                    .append(type, other.type)
                    .isEquals();
            } else {
                return false;
            }
        }

        @Override
        public Locator getLocator() {
            return locator;
        }

        @Override
        public String getModule() {
            return locator.getModule();
        }

        @Override
        public String getName() {
            return locator.getName();
        }

        @Override
        public List<Type> getPossibleTypes() {
            return asList(type);
        }

        @Override
        public Type getType() {
            return type;
        }

        @Override
        public boolean hasType() {
            return true;
        }

        @Override
        public boolean isFunction() {
            return type.isFunction();
        }

        @Override
        public int hashCode() {
            return Objects.hash(locator, type);
        }

        @Override
        public String toString() {
            return "(" + locator + ":" + type + ")";
        }
    }

    private static final class UnboundState implements State {

        private final Locator locator;
        private final List<Type> possibleTypes;

        public UnboundState(Locator locator, List<Type> possibleTypes) {
            this.locator = locator;
            this.possibleTypes = ImmutableList.copyOf(possibleTypes);
        }

        @Override
        public boolean equals(Object o) {
            if (o == this) {
                return true;
            } else if (o instanceof UnboundState) {
                UnboundState other = (UnboundState) o;
                return new EqualsBuilder()
                    .append(locator, other.locator)
                    .isEquals();
            } else {
                return false;
            }
        }

        @Override
        public Locator getLocator() {
            return locator;
        }

        @Override
        public String getModule() {
            return locator.getModule();
        }

        @Override
        public String getName() {
            return locator.getName();
        }

        @Override
        public List<Type> getPossibleTypes() {
            return possibleTypes;
        }

        @Override
        public Type getType() {
            throw new IllegalStateException("Type is not bound");
        }

        @Override
        public boolean hasType() {
            return false;
        }

        @Override
        public boolean isFunction() {
            for (Type type : possibleTypes) {
                if (type.isFunction()) {
                    return true;
                }
            }
            return false;
        }

        @Override
        public int hashCode() {
            return Objects.hash(locator);
        }

        @Override
        public String toString() {
            return "(" + locator + "?" + possibleTypes + ")";
        }
    }
}
