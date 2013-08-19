package snacks.lang.compiler;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import snacks.lang.SnacksException;
import snacks.lang.compiler.ast.AstNode;
import snacks.lang.compiler.ast.Reference;
import snacks.lang.compiler.ast.Type;

public class TranslatorState {

    private final Registry registry;
    private final String module;
    private final Deque<List<AstNode>> collections;

    public TranslatorState(Registry registry, String module) {
        this.registry = registry;
        this.module = module;
        this.collections = new ArrayDeque<>();
    }

    public List<AstNode> acceptCollection() {
        return collections.pop();
    }

    public void beginCollection() {
        collections.push(new ArrayList<AstNode>());
    }

    public void collect(AstNode node) {
        collections.peek().add(node);
    }

    public String getModule() {
        return module;
    }

    public Reference getOperator(String operator, Type leftType, Type rightType) throws SnacksException {
        return registry.getOperator(operator, leftType, rightType);
    }

    public Reference reference(String value) throws SnacksException {
        return registry.getReference(value);
    }

    public void register(String name, Type type) {
        registry.add(new Reference(module, name, type));
    }
}
