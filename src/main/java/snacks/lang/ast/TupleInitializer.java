package snacks.lang.ast;

import static snacks.lang.Type.tuple;

import java.util.ArrayList;
import java.util.List;
import snacks.lang.Type;

public class TupleInitializer extends AstNode {

    private final List<AstNode> elements;

    public TupleInitializer(List<AstNode> elements) {
        this.elements = elements;
    }

    @Override
    public void generate(Generator generator) {
        generator.generateTupleInitializer(this);
    }

    public List<AstNode> getElements() {
        return elements;
    }

    @Override
    public Type getType() {
        List<Type> types = new ArrayList<>();
        for (AstNode element : elements) {
            types.add(element.getType());
        }
        return tuple(types);
    }
}
