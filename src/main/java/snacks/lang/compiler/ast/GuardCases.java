package snacks.lang.compiler.ast;

import static snacks.lang.compiler.ast.Type.set;

import java.util.*;

public class GuardCases implements AstNode {

    private final List<AstNode> cases;

    public GuardCases(Collection<AstNode> cases) {
        this.cases = new ArrayList<>(cases);
    }

    @Override
    public void accept(AstVisitor visitor) {
        visitor.visitGuardCases(this);
    }

    public List<AstNode> getCases() {
        return cases;
    }

    @Override
    public Type getType() {
        Set<Type> types = new HashSet<>();
        for (AstNode c : cases) {
            types.add(c.getType());
        }
        return set(types);
    }

    @Override
    public boolean isInvokable() {
        return false;
    }
}
