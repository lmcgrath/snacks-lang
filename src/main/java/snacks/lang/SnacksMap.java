package snacks.lang;

import static java.util.Arrays.asList;
import static snacks.lang.SnackKind.EXPRESSION;
import static snacks.lang.SnackKind.TYPE;
import static snacks.lang.Types.*;

@Snack(name = "Map", kind = TYPE)
public abstract class SnacksMap<K, V> {

    private static final Type K_TYPE = var("snacks.lang.Map#k");
    private static final Type V_TYPE = var("snacks.lang.Map#v");

    @SnackType
    public static Type type() {
        return algebraic("snacks.lang.Map", asList(K_TYPE, V_TYPE), asList(
            EmptyMap.type(),
            MapNode.type()
        ));
    }

    public abstract K getKey();

    public abstract SnacksMap<K, V> getLeft();

    public abstract SnacksMap<K, V> getRight();

    public abstract Integer getSize();

    public abstract V getValue();

    public abstract boolean isEmpty();

    @Snack(name = "EmptyMap", kind = EXPRESSION)
    public static final class EmptyMapConstructor {

        public static Object instance() {
            return EmptyMap.value();
        }

        @SnackType
        public static Type type() {
            return EmptyMap.type();
        }
    }

    @Snack(name = "EmptyMap", kind = TYPE)
    public static final class EmptyMap<K, V> extends SnacksMap<K, V> {

        private static EmptyMap value;

        @SnackType
        public static Type type() {
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
        public Integer getSize() {
            return 0;
        }

        @Override
        public V getValue() {
            throw new SnacksException("Cannot get value from empty map!");
        }

        @Override
        public boolean isEmpty() {
            return true;
        }
    }

    @Snack(name = "MapNode", kind = EXPRESSION)
    public static final class MapNodeConstructor {

        private static MapNodeConstructor instance;

        public static MapNodeConstructor instance() {
            if (instance == null) {
                instance = new MapNodeConstructor();
            }
            return instance;
        }

        @SnackType
        public static Type type() {
            return func(integerType(), func(K_TYPE, func(V_TYPE, func(SnacksMap.type(), func(
                SnacksMap.type(),
                MapNode.type()
            )))));
        }

        public Object apply(Integer size) {
            return new Closure1(size);
        }

        public static final class Closure1 {

            private final Integer size;

            public Closure1(Integer size) {
                this.size = size;
            }

            public Object apply(Object key) {
                return new Closure2(size, key);
            }
        }

        public static final class Closure2 {

            private final Integer size;
            private final Object key;

            public Closure2(Integer size, Object key) {
                this.size = size;
                this.key = key;
            }

            public Object apply(Object value) {
                return new Closure3(size, key, value);
            }
        }

        public static final class Closure3 {

            private final Integer size;
            private final Object key;
            private final Object value;

            public Closure3(Integer size, Object key, Object value) {
                this.size = size;
                this.key = key;
                this.value = value;
            }

            public Object apply(SnacksMap left) {
                return new Closure4(size, key, value, left);
            }
        }

        public static final class Closure4 {

            private final Integer size;
            private final Object key;
            private final Object value;
            private final SnacksMap left;

            public Closure4(Integer size, Object key, Object value, SnacksMap left) {
                this.size = size;
                this.key = key;
                this.value = value;
                this.left = left;
            }

            @SuppressWarnings("unchecked")
            public Object apply(SnacksMap right) {
                return new MapNode(size, key, value, left, right);
            }
        }
    }

    @Snack(name = "MapNode", kind = TYPE, arguments = { "snacks.lang.Map#k", "snacks.lang.Map#k" })
    public static final class MapNode<K, V> extends SnacksMap<K, V> {

        @SnackType
        public static Type type() {
            return record("snacks.lang.MapNode", asList(K_TYPE, V_TYPE), asList(
                property("size", integerType()),
                property("key", K_TYPE),
                property("value", V_TYPE),
                property("left", recur("snacks.lang.Map", asList(K_TYPE, V_TYPE))),
                property("right", recur("snacks.lang.Map", asList(K_TYPE, V_TYPE)))
            ));
        }

        private final Integer size;
        private final K key;
        private final V value;
        private final SnacksMap<K, V> left;
        private final SnacksMap<K, V> right;

        public MapNode(Integer size, K key, V value, SnacksMap<K, V> left, SnacksMap<K, V> right) {
            this.size = size;
            this.key = key;
            this.value = value;
            this.left = left;
            this.right = right;
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
        public Integer getSize() {
            return size;
        }

        @Override
        public V getValue() {
            return value;
        }

        @Override
        public boolean isEmpty() {
            return false;
        }
    }
}
