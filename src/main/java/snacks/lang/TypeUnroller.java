package snacks.lang;

import static snacks.lang.Type.*;
import static snacks.lang.Types.recur;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import snacks.lang.Type.*;
import snacks.lang.Type.RecordType.Property;

public class TypeUnroller implements TypeGenerator {

    private final Type childType;
    private final Type parentType;
    private Type result;

    public TypeUnroller(Type childType, Type parentType) {
        this.childType = childType;
        this.parentType = parentType;
    }

    @Override
    public void generateAlgebraicType(AlgebraicType type) {
        List<Type> arguments;
        if (Objects.equals(parentType.getName(), type.getName())) {
            arguments = unroll(parentType.getArguments());
        } else {
            arguments = unroll(type.getArguments());
        }
        result = new AlgebraicType(type.getName(), arguments, unroll(type.getOptions()));
    }

    @Override
    public void generateFunctionType(FunctionType type) {
        result = type;
    }

    @Override
    public void generateRecordType(RecordType type) {
        List<Type> arguments = new ArrayList<>();
        for (Type argument : type.getArguments()) {
            arguments.add(unroll(argument));
        }
        List<Property> properties = new ArrayList<>();
        for (Property property : type.getProperties()) {
            properties.add(new Property(property.getName(), unroll(property.getType())));
        }
        result = new RecordType(type.getName(), arguments, properties);
    }

    @Override
    public void generateRecursiveType(RecursiveType type) {
        if (Objects.equals(parentType.getName(), type.getName())) {
            result = unroll(parentType);
        } else {
            result = type;
        }
    }

    @Override
    public void generateSimpleType(SimpleType type) {
        result = type;
    }

    @Override
    public void generateUnionType(UnionType type) {
        result = type;
    }

    @Override
    public void generateVariableType(VariableType type) {
        result = type;
    }

    public Type unroll() {
        result = null;
        childType.generate(this);
        return result;
    }

    private List<Type> unroll(Iterable<Type> types) {
        List<Type> unrolledTypes = new ArrayList<>();
        for (Type type : types) {
            unrolledTypes.add(unroll(type));
        }
        return unrolledTypes;
    }

    private Type unroll(Type type) {
        result = null;
        if (Objects.equals(type.getName(), childType.getName())) {
            return recur(type.getName(), parentType.getArguments());
        } else {
            type.generate(this);
            return result;
        }
    }
}
