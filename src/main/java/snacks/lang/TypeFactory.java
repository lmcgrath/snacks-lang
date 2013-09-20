package snacks.lang;

import java.util.Map;

public interface TypeFactory {

    Type createVariable();

    Type genericCopy(TypeSet type, Map<Type, Type> mappings);

    Type genericCopy(TypeVariable type, Map<Type, Type> mappings);

    Type genericCopy(RecordType type, Map<Type, Type> mappings);

    Type genericCopy(FunctionType type, Map<Type, Type> mappings);

    Type genericCopy(SimpleType type, Map<Type, Type> mappings);

    Type genericCopy(PropertyType type, Map<Type, Type> mappings);
}
