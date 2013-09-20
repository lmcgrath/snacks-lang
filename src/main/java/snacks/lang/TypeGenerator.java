package snacks.lang;

public interface TypeGenerator {

    void generateFunctionType(FunctionType type);

    void generatePropertyType(PropertyType type);

    void generateSimpleType(SimpleType type);

    void generateRecordType(RecordType type);

    void generateTypeSet(TypeSet type);

    void generateTypeVariable(TypeVariable type);
}
