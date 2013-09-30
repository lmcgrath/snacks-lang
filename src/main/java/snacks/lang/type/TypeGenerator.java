package snacks.lang.type;

public interface TypeGenerator {

    void generateAlgebraicType(AlgebraicType type);

    void generateParameterizedType(ParameterizedType type);

    void generateFunctionType(FunctionType type);

    void generateSimpleType(SimpleType type);

    void generateRecordType(RecordType type);

    void generateUnionType(UnionType type);

    void generateVariableType(VariableType type);
}
