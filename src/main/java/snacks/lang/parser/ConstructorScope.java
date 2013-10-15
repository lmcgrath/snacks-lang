package snacks.lang.parser;

import static snacks.lang.ast.AstFactory.access;

import java.util.Objects;
import snacks.lang.ast.AstNode;
import snacks.lang.ast.Reference;
import snacks.lang.ast.UndefinedSymbolException;
import snacks.lang.type.RecordType;
import snacks.lang.type.RecordType.Property;

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
            if (Objects.equals(this.property, property.getName())) {
                return access(reference, property.getName(), property.getType());
            }
        }
        throw new UndefinedSymbolException("Property '" + this.property + "' does not exist");
    }

    public void setProperty(String property) {
        this.property = property;
    }
}
