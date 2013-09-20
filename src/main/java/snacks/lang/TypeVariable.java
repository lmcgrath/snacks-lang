package snacks.lang;

import static java.util.Arrays.asList;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class TypeVariable extends Type {

    private State state;

    public TypeVariable(String name) {
        this.state = new UnboundState(this, name);
    }

    @Override
    public void bind(Type type) {
        state.bind(type);
    }

    @Override
    public List<Type> decompose() {
        return state.decompose();
    }

    @Override
    public boolean equals(Object o) {
        return o == this || o instanceof TypeVariable && Objects.equals(state, ((TypeVariable) o).state);
    }

    @Override
    public Type expose() {
        return state.expose();
    }

    @Override
    public void generate(TypeGenerator generator) {
        generator.generateTypeVariable(this);
    }

    @Override
    public Type genericCopy(TypeFactory types, Map<Type, Type> mappings) {
        return types.genericCopyOfTypeVariable(this, mappings);
    }

    @Override
    public String getName() {
        return state.getName();
    }

    @Override
    public int hashCode() {
        return Objects.hash(state);
    }

    @Override
    public Type recompose(Type functionType, TypeFactory types) {
        return state.recompose(functionType, types);
    }

    @Override
    public String toString() {
        return state.toString();
    }

    @Override
    public boolean unifyLeft(Type other) {
        if (!equals(other)) {
            if (occursIn(other)) {
                return false;
            } else {
                bind(other);
            }
        }
        return true;
    }

    @Override
    public boolean unifyRight(Type other) {
        return unify(other);
    }

    @Override
    protected boolean contains(Type type) {
        return state.contains(type);
    }

    private interface State {

        void bind(Type type);

        boolean contains(Type type);

        List<Type> decompose();

        Type expose();

        String getName();

        Type recompose(Type functionType, TypeFactory environment);
    }

    private static final class BoundState implements State {

        private final Type type;

        public BoundState(Type type) {
            this.type = type;
        }

        @Override
        public void bind(Type type) {
            // intentionally empty
        }

        @Override
        public boolean contains(Type type) {
            return this.type.contains(type);
        }

        @Override
        public List<Type> decompose() {
            return type.decompose();
        }

        @Override
        public boolean equals(Object o) {
            return o instanceof BoundState && Objects.equals(type, ((BoundState) o).type);
        }

        @Override
        public Type expose() {
            return type.expose();
        }

        @Override
        public String getName() {
            return type.getName();
        }

        @Override
        public int hashCode() {
            return Objects.hash(type);
        }

        @Override
        public Type recompose(Type functionType, TypeFactory environment) {
            return type;
        }

        @Override
        public String toString() {
            return "<var " + type.toString() + ">";
        }
    }

    private static final class UnboundState implements State {

        private final TypeVariable parent;
        private final String name;

        public UnboundState(TypeVariable parent, String name) {
            this.parent = parent;
            this.name = name;
        }

        @Override
        public void bind(Type type) {
            parent.state = new BoundState(type);
        }

        @Override
        public boolean contains(Type type) {
            return false;
        }

        @Override
        public List<Type> decompose() {
            return asList((Type) parent);
        }

        @Override
        public boolean equals(Object o) {
            return o instanceof UnboundState && Objects.equals(name, ((UnboundState) o).name);
        }

        @Override
        public Type expose() {
            return parent;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public int hashCode() {
            return Objects.hash(name);
        }

        @Override
        public Type recompose(Type functionType, TypeFactory environment) {
            int size = functionType.decompose().size();
            List<Type> variables = new ArrayList<>();
            for (int i = 0; i < size; i++) {
                variables.add(environment.createVariable());
            }
            return set(variables);
        }

        @Override
        public String toString() {
            return "<var " + name + ">";
        }
    }
}
