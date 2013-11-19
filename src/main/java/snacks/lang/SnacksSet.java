package snacks.lang;

import static java.util.Arrays.asList;
import static snacks.lang.SnackKind.EXPRESSION;
import static snacks.lang.SnackKind.TYPE;
import static snacks.lang.SnacksList.listOf;
import static snacks.lang.SnacksSet.EmptySet.emptyType;
import static snacks.lang.SnacksSet.SetEntry.entryTypeOf;
import static snacks.lang.Types.*;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import org.apache.commons.lang.builder.EqualsBuilder;

@Snack(name = "Set", kind = TYPE, arguments = "snacks.lang.Set#a")
public abstract class SnacksSet<T> implements Iterable<T> {

    public static Type setOf(Type type) {
        return algebraic("snacks.lang.Set", asList(type), asList(
            emptyType(),
            entryTypeOf(type)
        ));
    }

    @SnackType
    public static Type setType() {
        return setOf(var("snacks.lang.Set#a"));
    }

    public static Type varType() {
        return var("snacks.lang.Set#a");
    }

    @Snack(name = "EmptySet", kind = TYPE)
    public static final class EmptySet<T> extends SnacksSet<T> {

        private static EmptySet value;

        @SnackType
        public static Type emptyType() {
            return simple("snacks.lang.EmptySet");
        }

        public static EmptySet value() {
            if (value == null) {
                value = new EmptySet();
            }
            return value;
        }

        @Override
        public boolean equals(Object o) {
            return o == this || o instanceof EmptySet;
        }

        @Override
        public int hashCode() {
            return Objects.hash();
        }

        @Override
        public Iterator<T> iterator() {
            return new Iterator<T>() {
                @Override
                public boolean hasNext() {
                    return false;
                }

                @Override
                public T next() {
                    throw new NoSuchElementException();
                }

                @Override
                public void remove() {
                    throw new UnsupportedOperationException();
                }
            };
        }

        @Override
        public String toString() {
            return "{,}";
        }

        @Snack(name = "EmptySet", kind = EXPRESSION)
        public static final class Constructor {

            @SnackType
            public static Type type() {
                return emptyType();
            }

            public static Object instance() {
                return EmptySet.value();
            }
        }
    }

    @Snack(name = "SetEntry", kind = TYPE, arguments = "snacks.lang.Set#a")
    public static final class SetEntry<T> extends SnacksSet<T> {

        @SnackType
        public static Type entryType() {
            return entryTypeOf(varType());
        }

        public static Type entryTypeOf(Type type) {
            return record("snacks.lang.SetEntry", asList(type), asList(
                property("hash", integerType()),
                property("elements", listOf(varType())),
                property("left", recur("snacks.lang.Set", asList(type))),
                property("right", recur("snacks.lang.Set", asList(type)))
            ));
        }

        private final Integer hash;
        private final SnacksList<T> elements;
        private final SnacksSet<T> left;
        private final SnacksSet<T> right;

        public SetEntry(Integer hash, SnacksList<T> elements, SnacksSet<T> left, SnacksSet<T> right) {
            this.hash = hash;
            this.elements = elements;
            this.left = left;
            this.right = right;
        }

        @Override
        public boolean equals(Object o) {
            if (o == this) {
                return true;
            } else if (o instanceof SetEntry) {
                SetEntry other = (SetEntry) o;
                return new EqualsBuilder()
                    .append(hash, other.hash)
                    .append(elements, other.elements)
                    .append(left, other.left)
                    .append(right, other.right)
                    .isEquals();
            } else {
                return false;
            }
        }

        public SnacksList<T> getElements() {
            return elements;
        }

        public Integer getHash() {
            return hash;
        }

        public SnacksSet<T> getLeft() {
            return left;
        }

        public SnacksSet<T> getRight() {
            return right;
        }

        @Override
        public int hashCode() {
            return Objects.hash(hash, elements, left, right);
        }

        @Override
        public Iterator<T> iterator() {
            return new IteratorImpl<>(this);
        }

        @Override
        public String toString() {
            Iterator<T> entries = iterator();
            StringBuilder builder = new StringBuilder();
            builder.append("{ ");
            builder.append(entries.next());
            while (entries.hasNext()) {
                builder.append(", ");
                builder.append(entries.next());
            }
            builder.append(" }");
            return builder.toString();
        }

        @Snack(name = "SetEntry", kind = EXPRESSION, arguments = "snacks.lang.Set#a")
        public static final class Constructor<T> {

            private static Constructor instance;

            public static Object instance() {
                if (instance == null) {
                    instance = new Constructor();
                }
                return instance;
            }

            @SnackType
            public static Type type() {
                return func(integerType(), func(listOf(varType()), func(setType(), func(setType(), entryType()))));
            }

            public Object apply(Integer hash) {
                return new Closure1<T>(hash);
            }

            public static final class Closure1<T> {

                private final Integer hash;

                public Closure1(Integer hash) {
                    this.hash = hash;
                }

                public Object apply(SnacksList<T> elements) {
                    return new Closure2<>(hash, elements);
                }
            }

            public static final class Closure2<T> {

                private final Integer hash;
                private final SnacksList<T> elements;

                public Closure2(Integer hash, SnacksList<T> elements) {
                    this.hash = hash;
                    this.elements = elements;
                }

                public Object apply(SnacksSet<T> left) {
                    return new Closure3<>(hash, elements, left);
                }
            }

            public static final class Closure3<T> {

                private final Integer hash;
                private final SnacksList<T> elements;
                private final SnacksSet<T> left;

                public Closure3(Integer hash, SnacksList<T> elements, SnacksSet<T> left) {
                    this.hash = hash;
                    this.elements = elements;
                    this.left = left;
                }

                public Object apply(SnacksSet<T> right) {
                    return new SetEntry<>(hash, elements, left, right);
                }
            }
        }
    }

    private static final class IteratorImpl<T> implements Iterator<T> {

        private State<T> state;

        public IteratorImpl(SnacksSet<T> set) {
            this.state = new State<>(set);
        }

        @Override
        public boolean hasNext() {
            return state.hasNext() || state.hasMore();
        }

        @Override
        public T next() {
            if (state.hasNext()) {
                return state.next();
            } else if (state.hasMore()) {
                state = state.more();
                return state.next();
            } else {
                throw new NoSuchElementException();
            }
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }

        private static final class State<T> {

            private final SnacksSet<T> set;
            private final Iterator<T> iterator;

            public State(SnacksSet<T> set) {
                this.set = set;
                if (set instanceof SetEntry) {
                    this.iterator = ((SetEntry<T>) set).getElements().iterator();
                } else {
                    this.iterator = null;
                }
            }

            public boolean hasNext() {
                return iterator != null && iterator.hasNext();
            }

            public T next() {
                if (iterator == null) {
                    throw new NoSuchElementException();
                } else {
                    return iterator.next();
                }
            }

            public boolean hasMore() {
                return set instanceof SetEntry && ((SetEntry) set).getLeft() instanceof SetEntry;
            }

            public State<T> more() {
                if (hasMore()) {
                    return new State<>(((SetEntry<T>) set).getLeft());
                } else {
                    throw new NoSuchElementException();
                }
            }
        }
    }
}
