package snacks.lang;

public interface SnacksRegistry {

    Class<?> classOf(String qualifiedName, SnackKind kind);

    Operator getOperator(String name);

    boolean isOperator(String name);

    Type typeOf(String qualifiedName, SnackKind kind);
}
