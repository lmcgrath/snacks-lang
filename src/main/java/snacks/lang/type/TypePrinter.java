package snacks.lang.type;

public interface TypePrinter {

    void print(Type type);

    void printAlgebraicType(AlgebraicType type);

    void printFunctionType(FunctionType type);

    void printRecordType(RecordType type);

    void printRecursiveType(RecursiveType type);

    void printSimpleType(SimpleType type);

    void printUnionType(UnionType type);

    void printVariableType(VariableType type);
}
