package snacks.lang;

import static java.util.Arrays.asList;
import static snacks.lang.SnackKind.EXPRESSION;
import static snacks.lang.SnackKind.TYPE;
import static snacks.lang.Types.*;

import java.util.*;
import org.apache.commons.lang.builder.EqualsBuilder;

@Snack(name = "List", kind = TYPE, arguments = "snacks.lang.List#a")
public abstract class SnacksList<T> implements Iterable<T> {

    public static <T> List<T> fromList(SnacksList<T> list) {
        List<T> result = new ArrayList<>();
        SnacksList<T> tail = list;
        while (true) {
            if (tail instanceof ListEntry) {
                ListEntry<T> element = (ListEntry<T>) tail;
                result.add(element.get_0());
                tail = element.get_1();
            } else if (tail instanceof EmptyList) {
                break;
            }
        }
        return result;
    }

    public static <T> SnacksList<T> toList(Iterable<T> iterable) {
        if (iterable instanceof SnacksList) {
            return (SnacksList<T>) iterable;
        } else {
            List<T> list = new ArrayList<>();
            for (T t : iterable) {
                list.add(t);
            }
            return toList(list);
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> SnacksList<T> toList(T... elements) {
        SnacksList<T> tail = EmptyList.value();
        for (int i = elements.length - 1; i >= 0; i--) {
            tail = new ListEntry<>(elements[i], tail);
        }
        return tail;
    }

    public static <T> SnacksList<T> toList(Collection<T> elements) {
        List<T> list = new ArrayList<>(elements);
        SnacksList<T> tail = EmptyList.value();
        for (int i = list.size() - 1; i >= 0; i--) {
            tail = new ListEntry<>(list.get(i), tail);
        }
        return tail;
    }

    @SnackType
    public static Type listType() {
        return listOf(var("snacks.lang.List#a"));
    }

    public static Type listOf(Type type) {
        return algebraic("snacks.lang.List", asList(type), asList(
            simple("snacks.lang.EmptyList"),
            record("snacks.lang.ListEntry", asList(type), asList(
                property("_0", type),
                property("_1", recur("snacks.lang.List", asList(type)))
            ))
        ));
    }

    private SnacksList() {
        // intentionally empty
    }

    public abstract boolean isEmpty();

    public abstract SnacksList<T> reverse();

    public abstract int size();

    @Snack(name = "ListEntry", kind = TYPE, arguments = "snacks.lang.List#a")
    public static final class ListEntry<T> extends SnacksList<T> {

        @SnackType
        public static Type type() {
            return record("snacks.lang.ListEntry", asList(var("snacks.lang.List#a")), asList(
                property("_0", var("snacks.lang.List#a")),
                property("_1", algebraic("snacks.lang.List", asList(var("snacks.lang.List#a")), asList(
                    simple("snacks.lang.EmptyList"),
                    recur("snacks.lang.ListEntry", asList(var("snacks.lang.List#a")))
                )))
            ));
        }

        @Override
        public boolean isEmpty() {
            return false;
        }

        @Override
        public Iterator<T> iterator() {
            return new IteratorImpl<>(this);
        }

        @Override
        public SnacksList<T> reverse() {
            List<T> ts = new ArrayList<>();
            for (T t : this) {
                ts.add(t);
            }
            Collections.reverse(ts);
            return toList(ts);
        }

        @Override
        public int size() {
            return 1 + _1.size();
        }

        private final T _0;
        private final SnacksList<T> _1;

        public ListEntry(T _0, SnacksList<T> _1) {
            this._0 = _0;
            this._1 = _1;
        }

        @Override
        public boolean equals(Object o) {
            if (o == this) {
                return true;
            } else if (o instanceof ListEntry) {
                ListEntry other = (ListEntry) o;
                return new EqualsBuilder()
                    .append(_0, other._0)
                    .append(_1, other._1)
                    .isEquals();
            } else {
                return false;
            }
        }

        public T get_0() {
            return _0;
        }

        public SnacksList<T> get_1() {
            return _1;
        }

        @Override
        public int hashCode() {
            return Objects.hash(_0, _1);
        }

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            builder.append("[");
            builder.append(_0);
            SnacksList remainder = _1;
            if (!(remainder instanceof EmptyList)) {
                while (!(remainder instanceof EmptyList)) {
                    builder.append(", ");
                    ListEntry tail = (ListEntry) remainder;
                    builder.append(tail._0);
                    remainder = tail._1;
                }
            }
            builder.append("]");
            return builder.toString();
        }

        @Snack(name = "ListEntry", kind = EXPRESSION)
        public static final class Constructor {

            private static Constructor instance;

            public static Object instance() {
                if (instance == null) {
                    instance = new Constructor();
                }
                return instance;
            }

            @SnackType
            public static Type type() {
                return func(var("snacks.lang.List#a"), func(SnacksList.listType(), ListEntry.type()));
            }

            public Object apply(Object value) {
                return new Closure(value);
            }

            public static final class Closure {

                private final Object value;

                public Closure(Object value) {
                    this.value = value;
                }

                @SuppressWarnings("unchecked")
                public Object apply(Object tail) {
                    return new ListEntry(value, (SnacksList) tail);
                }
            }
        }
    }

    @Snack(name = "EmptyList", kind = TYPE)
    public static final class EmptyList<T> extends SnacksList<T> {

        private static EmptyList value;

        @SuppressWarnings("unchecked")
        public static <T> EmptyList<T> value() {
            if (value == null) {
                value = new EmptyList();
            }
            return value;
        }

        @SnackType
        public static Type type() {
            return simple("snacks.lang.EmptyList");
        }

        @Override
        public boolean isEmpty() {
            return true;
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
        public SnacksList<T> reverse() {
            return this;
        }

        @Override
        public int size() {
            return 0;
        }

        @Override
        public boolean equals(Object o) {
            return o == this || o instanceof EmptyList;
        }

        @Override
        public int hashCode() {
            return Objects.hash();
        }

        @Override
        public String toString() {
            return "[]";
        }

        @Snack(name = "EmptyList", kind = EXPRESSION)
        public static final class Constructor {

            public static Object instance() {
                return value();
            }

            @SnackType
            public static Type type() {
                return EmptyList.type();
            }
        }
    }

    private static final class IteratorImpl<T> implements Iterator<T> {

        private SnacksList<T> tail;

        public IteratorImpl(SnacksList<T> tail) {
            this.tail = tail;
        }

        @Override
        public boolean hasNext() {
            return tail instanceof ListEntry;
        }

        @Override
        public T next() {
            if (tail instanceof ListEntry) {
                ListEntry<T> element = (ListEntry<T>) tail;
                T data = element.get_0();
                tail = element.get_1();
                return data;
            } else if (tail instanceof EmptyList) {
                throw new NoSuchElementException();
            } else {
                throw new MatchException("Could not match " + tail.getClass().getName());
            }
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
}
