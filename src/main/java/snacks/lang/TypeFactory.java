package snacks.lang;

import static snacks.lang.Type.*;

import java.util.Map;
import snacks.lang.Type.VariableType;

public interface TypeFactory {

    Type copyAlgebraicType(AlgebraicType type, Map<Type, Type> mappings);

    Type copyFunctionType(FunctionType type, Map<Type, Type> mappings);

    Type copyRecordType(RecordType type, Map<Type, Type> mappings);

    Type copyRecursiveType(RecursiveType type, Map<Type, Type> mappings);

    Type copySimpleType(SimpleType type, Map<Type, Type> mappings);

    Type copyUnionType(UnionType type, Map<Type, Type> mappings);

    Type copyVariableType(VariableType type, Map<Type, Type> mappings);

    Type createVariable();

    Type expand(RecursiveType type);

    boolean unify(Type left, Type right);
}
