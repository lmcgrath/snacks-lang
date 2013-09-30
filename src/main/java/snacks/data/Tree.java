package snacks.data;

import static java.util.Arrays.asList;
import static snacks.lang.SnackKind.EXPRESSION;
import static snacks.lang.SnackKind.TYPE;
import static snacks.lang.type.Types.*;

import snacks.lang.Snack;
import snacks.lang.SnackType;
import snacks.lang.Tuple3;
import snacks.lang.type.Type;

@Snack(name = "Tree", kind = TYPE, parameters = "data")
public interface Tree {

    @Snack(name = "Node", kind = EXPRESSION)
    static final class NodeConstructor {

        private static NodeConstructor instance;

        public static NodeConstructor instance() {
            if (instance == null) {
                instance = new NodeConstructor();
            }
            return instance;
        }

        @SnackType
        public static Type type() {
            return func(Types.dataType(), func(Types.treeType(), func(Types.treeType(), Types.nodeType())));
        }

        public NodeConstructor_0 apply(Object data) {
            return new NodeConstructor_0(data);
        }
    }

    static final class NodeConstructor_0 {

        private final Object data;

        public NodeConstructor_0(Object data) {
            this.data = data;
        }

        public NodeConstructor_1 apply(Tree left) {
            return new NodeConstructor_1(data, left);
        }
    }

    static final class NodeConstructor_1 {

        private final Object data;
        private final Tree left;

        public NodeConstructor_1(Object data, Tree left) {
            this.data = data;
            this.left = left;
        }

        public Node apply(Tree right) {
            return new Node(data, left, right);
        }
    }

    @Snack(name = "Node", kind = TYPE)
    static final class Node extends Tuple3<Object, Tree, Tree> implements Tree {

        @SnackType
        public static Type type() {
            return Types.nodeType();
        }

        public Node(Object data, Tree left, Tree right) {
            super(data, left, right);
        }
    }

    @Snack(name = "Leaf", kind = EXPRESSION)
    static final class Leaf implements Tree {

        private static Object instance;

        public static Object instance() {
            if (instance == null) {
                instance = new Leaf();
            }
            return instance;
        }
    }

    static final class Types {

        public static Type dataType() {
            return var("snacks.lang.Tree#data");
        }

        public static Type nodeType() {
            return record("snacks.data.Node", asList(
                property("_0", dataType()),
                property("_1", treeType()),
                property("_2", treeType())
            ));
        }

        public static Type treeType() {
            return parameterized(algebraic("snacks.lang.Tree"), var("snacks.lang.Tree#data"));
        }

        private Types() {
            // intentionally empty
        }
    }
}
