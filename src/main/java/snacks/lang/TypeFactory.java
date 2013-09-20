package snacks.lang;

import java.util.Map;

public interface TypeFactory {

    Type createVariable();

    Type genericCopyOfTypeSet(TypeSet type, Map<Type, Type> mappings);

    Type genericCopyOfTypeVariable(TypeVariable type, Map<Type, Type> mappings);

    Type genericCopyOfRecordType(RecordType type, Map<Type, Type> mappings);

    Type genericCopyOfFunctionType(FunctionType type, Map<Type, Type> mappings);

    Type genericCopyOfSimpleType(SimpleType type, Map<Type, Type> mappings);
}
