package snacks.lang.ast;

import static snacks.lang.Type.var;

import snacks.lang.Type;

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
