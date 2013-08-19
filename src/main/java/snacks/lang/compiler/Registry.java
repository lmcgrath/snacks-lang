package snacks.lang.compiler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import snacks.lang.SnacksException;
import snacks.lang.compiler.ast.Reference;
import snacks.lang.compiler.ast.Type;

public class Registry {

    private final Map<String, List<Reference>> references;

    public Registry() {
        references = new HashMap<>();
    }

    public void add(Reference reference) {
        if (!references.containsKey(reference.getName())) {
            references.put(reference.getName(), new ArrayList<Reference>());
        }
        references.get(reference.getName()).add(reference);
    }

    public Reference getOperator(String name, Type leftType, Type rightType) throws SnacksException {
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
        throw new UndefinedReferenceException(
            "Undefined operator: " + name + ":(" + leftType + " -> " + rightType + ")"
        );
    }

    public Reference getReference(String identifier) {
        return references.get(identifier).get(0);
    }
}
