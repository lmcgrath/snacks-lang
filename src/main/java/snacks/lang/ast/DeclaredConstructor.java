package snacks.lang.ast;

import static snacks.lang.SnackKind.EXPRESSION;

import java.util.Objects;
import org.apache.commons.lang.builder.EqualsBuilder;
import snacks.lang.SnackKind;
import snacks.lang.type.Type;

public class DeclaredConstructor extends NamedNode {

    private final String qualifiedName;
    private final AstNode body;

    public DeclaredConstructor(String qualifiedName, AstNode body) {
        this.qualifiedName = qualifiedName;
        this.body = body;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (o instanceof DeclaredConstructor) {
            DeclaredConstructor other = (DeclaredConstructor) o;
            return new EqualsBuilder()
                .append(qualifiedName, other.qualifiedName)
                .append(body, other.body)
                .isEquals();
        } else {
            return false;
        }
    }

    @Override
    public void generate(Generator generator) {
        generator.generateDeclaredConstructor(this);
    }

    public AstNode getBody() {
        return body;
    }

    @Override
    public SnackKind getKind() {
        return EXPRESSION;
    }

    @Override
    public String getQualifiedName() {
        return qualifiedName + "Constructor";
    }

    @Override
    public Type getType() {
        return body.getType();
    }

    @Override
    public int hashCode() {
        return Objects.hash(qualifiedName, body);
    }

    @Override
    public void print(AstPrinter printer) {
        printer.printDeclaredConstructor(this);
    }

    @Override
    public Locator locator() {
        return new DeclarationLocator(qualifiedName, getKind());
    }

    @Override
    public String toString() {
        return "(Constructor " + qualifiedName + " " + body + ")";
    }
}
