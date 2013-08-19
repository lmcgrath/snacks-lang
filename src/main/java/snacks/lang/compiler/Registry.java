package snacks.lang.compiler;

import static snacks.lang.compiler.AstFactory.reference;
import static snacks.lang.compiler.ast.Type.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import snacks.lang.SnacksException;
import snacks.lang.compiler.ast.Reference;
import snacks.lang.compiler.ast.Type;

public class Registry {

    private static final Map<String, List<Reference>> builtin = new HashMap<>();

    static {
        op("+", func(BOOLEAN_TYPE, func(STRING_TYPE, STRING_TYPE)));
        op("+", func(INTEGER_TYPE, func(INTEGER_TYPE, INTEGER_TYPE)));
        op("+", func(INTEGER_TYPE, func(DOUBLE_TYPE, DOUBLE_TYPE)));
        op("+", func(INTEGER_TYPE, func(STRING_TYPE, STRING_TYPE)));
        op("+", func(DOUBLE_TYPE, func(DOUBLE_TYPE, DOUBLE_TYPE)));
        op("+", func(DOUBLE_TYPE, func(INTEGER_TYPE, DOUBLE_TYPE)));
        op("+", func(DOUBLE_TYPE, func(STRING_TYPE, STRING_TYPE)));
        op("+", func(STRING_TYPE, func(STRING_TYPE, STRING_TYPE)));
        op("+", func(STRING_TYPE, func(INTEGER_TYPE, STRING_TYPE)));
        op("+", func(STRING_TYPE, func(DOUBLE_TYPE, STRING_TYPE)));
        op("+", func(STRING_TYPE, func(BOOLEAN_TYPE, STRING_TYPE)));

        op("-", func(INTEGER_TYPE, func(INTEGER_TYPE, INTEGER_TYPE)));
        op("-", func(INTEGER_TYPE, func(DOUBLE_TYPE, DOUBLE_TYPE)));
        op("-", func(DOUBLE_TYPE, func(DOUBLE_TYPE, DOUBLE_TYPE)));
        op("-", func(DOUBLE_TYPE, func(INTEGER_TYPE, DOUBLE_TYPE)));

        op("*", func(INTEGER_TYPE, func(INTEGER_TYPE, INTEGER_TYPE)));
        op("*", func(INTEGER_TYPE, func(DOUBLE_TYPE, DOUBLE_TYPE)));
        op("*", func(DOUBLE_TYPE, func(DOUBLE_TYPE, DOUBLE_TYPE)));
        op("*", func(DOUBLE_TYPE, func(INTEGER_TYPE, DOUBLE_TYPE)));
        op("*", func(STRING_TYPE, func(INTEGER_TYPE, STRING_TYPE)));

        op("/", func(INTEGER_TYPE, func(INTEGER_TYPE, DOUBLE_TYPE)));
        op("/", func(INTEGER_TYPE, func(DOUBLE_TYPE, DOUBLE_TYPE)));
        op("/", func(DOUBLE_TYPE, func(DOUBLE_TYPE, DOUBLE_TYPE)));
        op("/", func(DOUBLE_TYPE, func(INTEGER_TYPE, DOUBLE_TYPE)));

        op("%", func(INTEGER_TYPE, func(INTEGER_TYPE, INTEGER_TYPE)));
    }

    private static void op(String operator, Type type) {
        if (!builtin.containsKey(operator)) {
            builtin.put(operator, new ArrayList<Reference>());
        }
        builtin.get(operator).add(reference("snacks/lang", operator, type));
    }

    private final Map<String, List<Reference>> references = new HashMap<>(builtin);

    public void add(Reference reference) {
        if (!references.containsKey(reference.getName())) {
            references.put(reference.getName(), new ArrayList<Reference>());
        }
        references.get(reference.getName()).add(reference);
    }

    public Reference getOperator(String name, Type leftType, Type rightType) throws SnacksException {
        if (references.containsKey(name)) {
            for (Reference reference : references.get(name)) {
                Type type = reference.getType();
                List<Type> parameters = type.getParameters();
                if (parameters.size() == 2) {
                    Type arg0 = parameters.get(0);
                    Type arg1 = parameters.get(1);
                    if (arg0.equals(leftType) && arg1.getParameters().get(0).equals(rightType)) {
                        return reference;
                    }
                }
            }
        }
        throw new UndefinedReferenceException("Undefined operator: " + name + ":" + func(leftType, rightType));
    }

    public Reference getReference(String identifier) {
        return references.get(identifier).get(0);
    }
}
