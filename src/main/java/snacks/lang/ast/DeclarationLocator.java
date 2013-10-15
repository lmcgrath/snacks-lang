package snacks.lang.ast;

import static snacks.lang.JavaUtils.javaName;
import static snacks.lang.SnackKind.EXPRESSION;

import java.util.Objects;
import org.apache.commons.lang.builder.EqualsBuilder;
import snacks.lang.SnackKind;

public class DeclarationLocator extends Locator {

    private final String qualifiedName;
    private final SnackKind kind;

    public DeclarationLocator(String qualifiedName) {
        this(qualifiedName, EXPRESSION);
    }

    public DeclarationLocator(String qualifiedName, SnackKind kind) {
        this.qualifiedName = qualifiedName;
        this.kind = kind;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (o instanceof DeclarationLocator) {
            DeclarationLocator other = (DeclarationLocator) o;
            return new EqualsBuilder()
                .append(qualifiedName, other.qualifiedName)
                .append(kind, other.kind)
                .isEquals();
        } else {
            return false;
        }
    }

    @Override
    public void accept(LocatorVisitor visitor) {
        visitor.visitDeclarationLocator(this);
    }

    @Override
    public void generate(Generator generator) {
        generator.generateDeclarationLocator(this);
    }

    public SnackKind getKind() {
        return kind;
    }

    @Override
    public String getName() {
        return qualifiedName;
    }

    @Override
    public int hashCode() {
        return Objects.hash(qualifiedName, kind);
    }

    public String getJavaName() {
        String module = qualifiedName.substring(0, qualifiedName.lastIndexOf('.'));
        String name = qualifiedName.substring(qualifiedName.lastIndexOf('.') + 1);
        return javaName(module).replace('.', '/') + '/' + javaName(name);
    }

    @Override
    public void print(AstPrinter printer) {
        printer.printDeclarationLocator(this);
    }

    @Override
    public String toString() {
        return qualifiedName + "(" + kind + ")";
    }
}
