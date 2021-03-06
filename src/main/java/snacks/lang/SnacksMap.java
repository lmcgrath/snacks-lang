package snacks.lang;

import static java.util.Arrays.asList;
import static snacks.lang.SnackKind.EXPRESSION;
import static snacks.lang.SnackKind.TYPE;
import static snacks.lang.SnacksMap.EmptyMap.emptyMap;
import static snacks.lang.SnacksMap.MapEntry.mapEntry;
import static snacks.lang.Types.*;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import org.apache.commons.lang.builder.EqualsBuilder;

@Snack(name = "Map", kind = TYPE)
public abstract class SnacksMap<K, V> implements Iterable<Tuple2<K, V>> {

    public static Type keyType() {
        return var("snacks.lang.Map#k");
    }

    @SnackType
    public static Type mapType() {
        return algebraic("snacks.lang.Map", asList(keyType(), valueType()), asList(
            emptyMap(),
            mapEntry()
        ));
    }

    public static Type valueType() {
        return var("snacks.lang.Map#v");
    }

    @Override
    public abstract boolean equals(Object o);

    public abstract K getKey();

    public abstract SnacksMap<K, V> getLeft();

    public abstract SnacksMap<K, V> getRight();

    public abstract V getValue();

    @Override
    public abstract int hashCode();

    public abstract boolean isEmpty();

    @Snack(name = "EmptyMap", kind = TYPE)
    public static final class EmptyMap<K, V> extends SnacksMap<K, V> {

        private static EmptyMap value;

        @SnackType
        public static Type emptyMap() {
            return simple("snacks.lang.EmptyMap");
        }

        @SuppressWarnings("unchecked")
        public static <K, V> EmptyMap<K, V> value() {
            if (value == null) {
                value = new EmptyMap();
            }
            return value;
        }

        @Override
        public boolean equals(Object o) {
            return o == this || o instanceof EmptyMap;
        }

        @Override
        public K getKey() {
            throw new SnacksException("Cannot get key from empty map!");
        }

        @Override
        public SnacksMap<K, V> getLeft() {
            throw new SnacksException("Cannot get left branch from empty map!");
        }

        @Override
        public SnacksMap<K, V> getRight() {
            throw new SnacksException("Cannot get right branch from empty map!");
        }

        @Override
        public V getValue() {
            throw new SnacksException("Cannot get value from empty map!");
        }

        @Override
        public int hashCode() {
            return Objects.hash();
        }

        @Override
        public boolean isEmpty() {
            return true;
        }

        @Override
        public Iterator<Tuple2<K, V>> iterator() {
            return new Iterator<Tuple2<K, V>>() {
                @Override
                public boolean hasNext() {
                    return false;
                }

                @Override
                public Tuple2<K, V> next() {
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
            return "{:}";
        }

        @Snack(name = "EmptyMap", kind = EXPRESSION)
        public static final class Constructor {

            public static Object instance() {
                return value();
            }

            @SnackType
            public static Type type() {
                return emptyMap();
            }
        }
    }

    @Snack(name = "MapEntry", kind = TYPE, arguments = { "snacks.lang.Map#k", "snacks.lang.Map#k" })
    public static final class MapEntry<K, V> extends SnacksMap<K, V> {

        @SnackType
        public static Type mapEntry() {
            return record("snacks.lang.MapEntry", asList(keyType(), valueType()), asList(
                property("key", keyType()),
                property("value", valueType()),
                property("left", recur("snacks.lang.Map", asList(keyType(), valueType()))),
                property("right", recur("snacks.lang.Map", asList(keyType(), valueType())))
            ));
        }

        private final K key;
        private final V value;
        private final SnacksMap<K, V> left;
        private final SnacksMap<K, V> right;

        public MapEntry(K key, V value, SnacksMap<K, V> left, SnacksMap<K, V> right) {
            this.key = key;
            this.value = value;
            this.left = left;
            this.right = right;
        }

        @Override
        public boolean equals(Object o) {
            if (o == this) {
                return true;
            } else if (o instanceof MapEntry) {
                MapEntry other = (MapEntry) o;
                return new EqualsBuilder()
                    .append(key, other.key)
                    .append(value, other.value)
                    .append(left, other.left)
                    .append(right, other.right)
                    .isEquals();
            } else {
                return false;
            }
        }

        @Override
        public K getKey() {
            return key;
        }

        @Override
        public SnacksMap<K, V> getLeft() {
            return left;
        }

        @Override
        public SnacksMap<K, V> getRight() {
            return right;
        }

        @Override
        public V getValue() {
            return value;
        }

        @Override
        public int hashCode() {
            return Objects.hash(key, value, left, right);
        }

        @Override
        public boolean isEmpty() {
            return false;
        }

        @Override
        public Iterator<Tuple2<K, V>> iterator() {
            return new IteratorImpl<>(this);
        }

        @Override
        public String toString() {
            Iterator<Tuple2<K, V>> entries = iterator();
            Tuple2<K, V> entry = entries.next();
            StringBuilder builder = new StringBuilder();
            builder.append("{ ");
            builder.append(entry.get_0());
            builder.append(" => ");
            builder.append(entry.get_1());
            while (entries.hasNext()) {
                entry = entries.next();
                builder.append(", ");
                builder.append(entry.get_0());
                builder.append(" => ");
                builder.append(entry.get_1());
            }
            builder.append(" }");
            return builder.toString();
        }

        @Snack(name = "MapEntry", kind = EXPRESSION)
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
                // MapEntry :: key -> value -> (Map key value) -> (Map key value)
                return func(keyType(), func(valueType(), func(mapType(), func(
                    mapType(),
                    mapEntry()
                ))));
            }

            public Object apply(Object key) {
                return new Closure1(key);
            }


            public static final class Closure1 {

                private final Object key;

                public Closure1(Object key) {
                    this.key = key;
                }

                public Object apply(Object value) {
                    return new Closure2(key, value);
                }
            }

            public static final class Closure2 {

                private final Object key;
                private final Object value;

                public Closure2(Object key, Object value) {
                    this.key = key;
                    this.value = value;
                }

                public Object apply(SnacksMap left) {
                    return new Closure3(key, value, left);
                }
            }

            public static final class Closure3 {

                private final Object key;
                private final Object value;
                private final SnacksMap left;

                public Closure3(Object key, Object value, SnacksMap left) {
                    this.key = key;
                    this.value = value;
                    this.left = left;
                }

                @SuppressWarnings("unchecked")
                public Object apply(SnacksMap right) {
                    return new MapEntry(key, value, left, right);
                }
            }
        }
    }

    private static final class IteratorImpl<K, V> implements Iterator<Tuple2<K, V>> {

        private SnacksMap<K, V> map;

        public IteratorImpl(SnacksMap<K, V> map) {
            this.map = map;
        }

        @Override
        public boolean hasNext() {
            return map instanceof MapEntry;
        }

        @Override
        public Tuple2<K, V> next() {
            if (map instanceof MapEntry) {
                MapEntry<K, V> entry = (MapEntry<K, V>) map;
                map = entry.getLeft();
                return new Tuple2<>(entry.getKey(), entry.getValue());
            } else if (map instanceof EmptyMap) {
                throw new NoSuchElementException();
            } else {
                throw new MatchException("Failed to match map: " + map.getClass().getName());
            }
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
}
