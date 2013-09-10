package snacks.lang.ast;

import static snacks.lang.Type.var;

import snacks.lang.Type;

public class Nop extends AstNode {

    public static final Nop INSTANCE = new Nop();

    private Nop() {
        // intentionally empty
    }

    @Override
    public void generate(Generator generator) {
        generator.generateNop(this);
    }

    @Override
    public Type getType() {
        return var("T");
    }
}