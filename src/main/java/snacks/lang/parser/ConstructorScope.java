package snacks.lang.parser;

import static snacks.lang.ast.AstFactory.access;

import java.util.Objects;
import snacks.lang.Type.RecordType;
import snacks.lang.Type.RecordType.Property;
import snacks.lang.ast.AstNode;
import snacks.lang.ast.Reference;
import snacks.lang.ast.UndefinedSymbolException;

class ConstructorScope {

    private final Reference reference;
    private final RecordType type;
    private String property;

    public ConstructorScope(Reference reference) {
        this.reference = reference;
        this.type = (RecordType) reference.getType(); // TODO hack
    }

    public AstNode accessProperty() {
        for (Property property : type.getProperties()) {
            if (Objects.equals(this.property, property.getName().getValue())) {
                return access(reference, property.getName().getValue(), property.getType());
            }
        }
        throw new UndefinedSymbolException("Property '" + this.property + "' does not exist");
    }

    public void setProperty(String property) {
        this.property = property;
    }
}
