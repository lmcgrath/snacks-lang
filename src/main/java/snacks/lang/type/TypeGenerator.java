package snacks.lang.type;

public interface TypeGenerator {

    void generateAlgebraicType(AlgebraicType type);

    void generateFunctionType(FunctionType type);

    void generateRecordType(RecordType type);

    void generateRecursiveType(RecursiveType type);

    void generateSimpleType(SimpleType type);

    void generateUnionType(UnionType type);

    void generateVariableType(VariableType type);
}
