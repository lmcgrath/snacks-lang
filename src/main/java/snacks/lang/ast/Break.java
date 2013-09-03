package snacks.lang.ast;

import static snacks.lang.ast.Type.var;

public class Break extends AstNode {

    public static final Break INSTANCE = new Break();

    private Break() {
        // intentionally empty
    }

    @Override
    public void generate(Generator generator) {
        generator.generateBreak(this);
    }

    @Override
    public Type getType() {
        return var("B");
    }
}
