package snacks.lang.ast;

import static snacks.lang.type.Types.VOID_TYPE;

import snacks.lang.type.Type;

public class UnitConstant extends AstNode {

    public static final UnitConstant INSTANCE = new UnitConstant();

    private UnitConstant() {
        // intentionally empty
    }

    @Override
    public void generate(Generator generator) {
        generator.generateUnitConstant(this);
    }

    @Override
    public Type getType() {
        return VOID_TYPE;
    }
}
