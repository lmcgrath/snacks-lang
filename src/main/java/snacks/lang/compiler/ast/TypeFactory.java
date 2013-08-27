package snacks.lang.compiler.ast;

import java.util.Map;

public interface TypeFactory {

    Type createVariable();

    Type genericCopy(TypeSet type, Map<Type, Type> mappings);

    Type genericCopy(TypeVariable type, Map<Type, Type> mappings);

    Type genericCopy(TypeOperator type, Map<Type, Type> mappings);
}
