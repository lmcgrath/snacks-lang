package snacks.lang.parser.syntax;

import java.util.List;
import beaver.Symbol;

public class ConstructorExpression extends VisitableSymbol {

    private final Symbol constructor;
    private final List<Symbol> arguments;

    public ConstructorExpression(Symbol constructor, List<Symbol> arguments) {
        this.constructor = constructor;
        this.arguments = arguments;
    }

    @Override
    public void accept(SyntaxVisitor visitor) {
        visitor.visitConstructorExpression(this);
    }

    public List<Symbol> getArguments() {
        return arguments;
    }

    public Symbol getConstructor() {
        return constructor;
    }
}
