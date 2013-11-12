package snacks.lang.ast;

import static snacks.lang.Types.union;

import java.util.*;
import snacks.lang.Type;

public class GuardCases extends AstNode {

    private final List<AstNode> cases;

    public GuardCases(Collection<AstNode> cases) {
        this.cases = new ArrayList<>(cases);
    }

    @Override
    public boolean equals(Object o) {
        return o == this || o instanceof GuardCases && Objects.equals(cases, ((GuardCases) o).cases);
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
        return union(types);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cases);
    }

    @Override
    public void print(AstPrinter printer) {
        printer.printGuardCases(this);
    }

    @Override
    public String toString() {
        return "(Guards " + cases + ")";
    }
}
