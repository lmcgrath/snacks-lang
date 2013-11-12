package snacks.lang;

import static snacks.lang.Type.*;

import snacks.lang.Type.FunctionType;

public interface TypeGenerator {

    void generateAlgebraicType(AlgebraicType type);

    void generateFunctionType(FunctionType type);

    void generateRecordType(RecordType type);

    void generateRecursiveType(RecursiveType type);

    void generateSimpleType(SimpleType type);

    void generateUnionType(UnionType type);

    void generateVariableType(VariableType type);
}
