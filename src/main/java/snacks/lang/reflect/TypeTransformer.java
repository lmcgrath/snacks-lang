package snacks.lang.reflect;

import static snacks.lang.type.Types.argumentOf;
import static snacks.lang.type.Types.resultOf;
import static snacks.lang.ListType.toList;

import java.util.ArrayList;
import java.util.List;
import snacks.lang.ListType;
import snacks.lang.MatchException;
import snacks.lang.Maybe.Nothing;
import snacks.lang.Symbol;
import snacks.lang.Tuple2;
import snacks.lang.reflect.TypeInfo.*;
import snacks.lang.type.*;
import snacks.lang.type.RecordType.Property;

public final class TypeTransformer {

    public static TypeInfo transform(Type type) {
        if (type instanceof FunctionType) {
            return new FunctionInfo(transform(argumentOf(type)), transform(resultOf(type)));
        } else if (type instanceof SimpleType) {
            return new SimpleInfo(Symbol.valueOf(type.getName()), Nothing.value());
        } else if (type instanceof VariableType) {
            return new VariableInfo(Symbol.valueOf(type.getName()));
        } else if (type instanceof AlgebraicType) {
            return new AlgebraicInfo(
                Symbol.valueOf(type.getName()),
                arguments(type.getArguments()),
                transformAll(((AlgebraicType) type).getOptions())
            );
        } else if (type instanceof RecordType) {
            return new RecordInfo(
                Symbol.valueOf(type.getName()),
                arguments(type.getArguments()),
                properties(((RecordType) type).getProperties()),
                Nothing.value()
            );
        } else {
            throw new MatchException("Could not match " + type.getClass().getName());
        }
    }

    private static ListType transformAll(List<Type> types) {
        List<TypeInfo> list = new ArrayList<>();
        for (Type type : types) {
            list.add(transform(type));
        }
        return ListType.toList(list);
    }

    private static ListType arguments(List<Type> arguments) {
        List<TypeInfo> list = new ArrayList<>();
        for (Type argument : arguments) {
            list.add(transform(argument));
        }
        return ListType.toList(list);
    }

    private static ListType properties(List<Property> properties) {
        List<Tuple2<Symbol, TypeInfo>> list = new ArrayList<>();
        for (Property property : properties) {
            list.add(new Tuple2<>(Symbol.valueOf(property.getName()), transform(property.getType())));
        }
        return ListType.toList(list);
    }

    private TypeTransformer() {
        // intentionally empty
    }
}
