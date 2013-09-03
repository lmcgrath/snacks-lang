package snacks.lang.ast;

import static snacks.lang.ast.Type.var;

public class Nillable extends AstNode {

    public static final Nillable INSTANCE = new Nillable();

    private Nillable() {
        // intentionally empty
    }

    @Override
    public void generate(Generator generator) {
        generator.generateNillable(this);
    }

    @Override
    public Type getType() {
        return var("T");
    }
}
