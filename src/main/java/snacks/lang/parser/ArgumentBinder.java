package snacks.lang.parser;

import static snacks.lang.Type.SimpleType;
import static snacks.lang.Type.UnionType;
import static snacks.lang.Type.VariableType;

import java.util.ArrayList;
import java.util.List;
import com.google.common.collect.ImmutableList;
import snacks.lang.Type;
import snacks.lang.TypeGenerator;
import snacks.lang.Type.AlgebraicType;
import snacks.lang.Type.FunctionType;
import snacks.lang.Type.RecordType;
import snacks.lang.Type.RecordType.Property;
import snacks.lang.Type.RecursiveType;

class ArgumentBinder implements TypeGenerator {

    private final List<Type> arguments;
    private Type target;

    public ArgumentBinder(Iterable<Type> arguments) {
        this.arguments = ImmutableList.copyOf(arguments);
    }

    public Type bind(Type type) {
        target = null;
        type.generate(this);
        return target;
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

    private List<Property> bindProperties(Iterable<Property> properties) {
        List<Property> boundProperties = new ArrayList<>();
        for (Property property : properties) {
            boundProperties.add(new Property(property.getName(), bind(property.getType())));
        }
        return boundProperties;
    }

    private List<Type> bindTypes(Iterable<Type> types) {
        List<Type> boundTypes = new ArrayList<>();
        for (Type type : types) {
            boundTypes.add(bind(type));
        }
        return boundTypes;
    }
}
