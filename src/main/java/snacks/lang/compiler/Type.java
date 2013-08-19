package snacks.lang.compiler;

import java.util.List;

public interface Type {

    void bind(Type type);

    Type expose();

    List<Type> getPossibilities();

    String getName();

    List<Type> getParameters();

    boolean isApplicableTo(Type type);

    boolean isFunction();

    boolean isParameterized();

    boolean isPossibility();

    boolean isVariable();
}
