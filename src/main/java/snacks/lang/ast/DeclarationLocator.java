package snacks.lang.ast;

import static org.apache.commons.lang.StringUtils.capitalize;
import static snacks.lang.JavaUtils.javaName;
import static snacks.lang.SnackKind.EXPRESSION;

import java.util.Objects;
import org.apache.commons.lang.builder.EqualsBuilder;
import snacks.lang.SnackKind;

public class DeclarationLocator extends Locator {

    private final String module;
    private final String name;
    private final SnackKind kind;

    public DeclarationLocator(String module, String name) {
        this(module, name, EXPRESSION);
    }

    public DeclarationLocator(String module, String name, SnackKind kind) {
        this.module = module;
        this.name = name;
        this.kind = kind;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (o instanceof DeclarationLocator) {
            DeclarationLocator other = (DeclarationLocator) o;
            return new EqualsBuilder()
                .append(module, other.module)
                .append(name, other.name)
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

    public String getModule() {
        return module;
    }

    @Override
    public String getName() {
        return name;
    }

    public String getQualifiedName() {
        return module + '.' + name;
    }

    @Override
    public int hashCode() {
        return Objects.hash(module, name, kind);
    }

    public String getJavaName() {
        return javaName(module).replace('.', '/') + '/' + capitalize(javaName(name));
    }

    @Override
    public void print(AstPrinter printer) {
        printer.printDeclarationLocator(this);
    }

    @Override
    public String toString() {
        return module + "#" + name + "(" + kind + ")";
    }
}
