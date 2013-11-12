package snacks.lang.ast;

import static snacks.lang.SnackKind.TYPE;
import static snacks.lang.Types.simple;

import java.util.Objects;
import snacks.lang.SnackKind;
import snacks.lang.Type;

public class DeclaredConstant extends NamedNode {

    private final String qualifiedName;

    public DeclaredConstant(String qualifiedName) {
        this.qualifiedName = qualifiedName;
    }

    @Override
    public boolean equals(Object o) {
        return o == this || o instanceof DeclaredConstant && Objects.equals(qualifiedName, ((DeclaredConstant) o).qualifiedName);
    }

    @Override
    public void generate(Generator generator) {
        generator.generateDeclaredConstant(this);
    }

    @Override
    public SnackKind getKind() {
        return TYPE;
    }

    @Override
    public String getQualifiedName() {
        return qualifiedName;
    }

    @Override
    public Type getType() {
        return simple(qualifiedName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(qualifiedName);
    }

    @Override
    public void print(AstPrinter printer) {
        printer.printDeclaredConstant(this);
    }

    @Override
    public String toString() {
        return "(Constant " + qualifiedName + ")";
    }
}
