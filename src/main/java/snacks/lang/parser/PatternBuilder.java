package snacks.lang.parser;

import static snacks.lang.ast.AstFactory.declaration;
import static snacks.lang.ast.AstFactory.patterns;

import java.util.ArrayList;
import java.util.List;
import snacks.lang.ast.AstNode;
import snacks.lang.ast.Locator;
import snacks.lang.ast.NamedNode;
import snacks.lang.type.Type;
import snacks.lang.type.TypeFactory;

class PatternBuilder {

    private final Locator locator;
    private final List<AstNode> patterns;
    private final Type type;

    public PatternBuilder(Locator locator, Type type) {
        this.locator = locator;
        this.patterns = new ArrayList<>();
        this.type = type;
    }

    public void addPattern(AstNode pattern, TypeFactory types) {
        if (types.unify(type, pattern.getType())) {
            patterns.add(pattern);
        } else {
            throw new TypeException("Type mismatch: " + pattern.getType() + " != " + type);
        }
    }

    public NamedNode toPattern() {
        return declaration(locator.getName(), patterns(type, patterns));
    }
}
