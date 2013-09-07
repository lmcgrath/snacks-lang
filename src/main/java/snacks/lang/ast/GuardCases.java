package snacks.lang.ast;

import static snacks.lang.Type.set;

import java.util.*;
import snacks.lang.Type;

public class GuardCases extends AstNode {

    private final List<AstNode> cases;

    public GuardCases(Collection<AstNode> cases) {
        this.cases = new ArrayList<>(cases);
    }

    @Override
    public void generate(Generator generator) {
        generator.generateGuardCases(this);
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
    public void print(AstPrinter printer) {
        printer.printGuardCases(this);
    }
}
