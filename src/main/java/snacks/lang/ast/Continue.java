package snacks.lang.ast;

import static snacks.lang.Type.var;

import snacks.lang.Type;

public class Continue extends AstNode {

    public static final Continue INSTANCE = new Continue();

    private Continue() {
        // intentionally empty
    }

    @Override
    public void generate(Generator generator) {
        generator.generateContinue(this);
    }

    @Override
    public Type getType() {
        return var("B");
    }
}
