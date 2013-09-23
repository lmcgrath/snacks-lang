package snacks.lang;

public interface SnacksRegistry {

    Class<?> classOf(String qualifiedName);

    Operator getOperator(String name);

    boolean isOperator(String name);

    Type typeOf(String qualifiedName);
}
