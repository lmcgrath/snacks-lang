package snacks.lang.parser;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import com.google.common.collect.ImmutableList;
import snacks.lang.type.*;
import snacks.lang.type.RecordType.Property;

class ArgumentBinder implements TypeGenerator {

    private final List<Type> arguments;
    private Type target;

    public ArgumentBinder(Collection<Type> arguments) {
        this.arguments = ImmutableList.copyOf(arguments);
    }

    public Type bind(Type type) {
        target = null;
        type.generate(this);
        return target;
    }

    private List<Type> bindTypes(Collection<Type> types) {
        List<Type> boundTypes = new ArrayList<>();
        for (Type type : types) {
            boundTypes.add(bind(type));
        }
        return boundTypes;
    }

    private List<Property> bindProperties(Collection<Property> properties) {
        List<Property> boundProperties = new ArrayList<>();
        for (Property property : properties) {
            boundProperties.add(new Property(property.getName(), bind(property.getType())));
        }
        return boundProperties;
    }

    @Override
    public void generateAlgebraicType(AlgebraicType type) {
        target = new AlgebraicType(type.getName(), arguments, bindTypes(type.getOptions()));
    }

    @Override
    public void generateFunctionType(FunctionType type) {
        target = type;
    }

    @Override
    public void generateRecordType(RecordType type) {
        target = new RecordType(type.getName(), arguments, bindProperties(type.getProperties()));
    }

    @Override
    public void generateRecursiveType(RecursiveType type) {
        target = new RecursiveType(type.getName(), arguments);
    }

    @Override
    public void generateSimpleType(SimpleType type) {
        target = type;
    }

    @Override
    public void generateUnionType(UnionType type) {
        target = new UnionType(bindTypes(type.getTypes()));
    }

    @Override
    public void generateVariableType(VariableType type) {
        if (type.expose() instanceof VariableType) {
            target = type;
        } else {
            target = new VariableType(bind(type.expose()));
        }
    }
}
