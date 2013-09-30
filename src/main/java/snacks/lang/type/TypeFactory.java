package snacks.lang.type;

import java.util.Map;

public interface TypeFactory {

    Type copyAlgebraicType(AlgebraicType type, Map<Type, Type> mappings);

    Type copyFunctionType(FunctionType type, Map<Type, Type> mappings);

    Type copyParameterizedType(ParameterizedType type, Map<Type, Type> mappings);

    Type copyRecordType(RecordType type, Map<Type, Type> mappings);

    Type copySimpleType(SimpleType type, Map<Type, Type> mappings);

    Type copyUnionType(UnionType type, Map<Type, Type> mappings);

    Type copyVariableType(VariableType type, Map<Type, Type> mappings);

    Type createVariable();
}
