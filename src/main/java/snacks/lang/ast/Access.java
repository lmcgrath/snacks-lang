package snacks.lang.ast;

import snacks.lang.Type;

public class Access extends AstNode {

    private final AstNode expression;
    private final String property;
    private final Type type;

    public Access(AstNode expression, String property, Type type) {
        this.expression = expression;
        this.property = property;
        this.type = type;
    }

    @Override
    public void generate(Generator generator) {
        generator.generateAccess(this);
    }

    @Override
    public Type getType() {
        return type;
    }

    public AstNode getExpression() {
        return expression;
    }

    public String getProperty() {
        return property;
    }
}
