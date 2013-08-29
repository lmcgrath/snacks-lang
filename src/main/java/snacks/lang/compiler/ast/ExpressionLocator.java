package snacks.lang.compiler.ast;

import java.util.Objects;
import org.apache.commons.lang.builder.EqualsBuilder;

public class ExpressionLocator implements Locator {

    private final String module;
    private final String name;

    public ExpressionLocator(String module, String name) {
        this.module = module;
        this.name = name;
    }

    @Override
    public void accept(AstVisitor visitor) {
        visitor.visitDeclarationLocator(this);
    }

    @Override
    public boolean isVariable() {
        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (o instanceof ExpressionLocator) {
            ExpressionLocator other = (ExpressionLocator) o;
            return new EqualsBuilder()
                .append(module, other.module)
                .append(name, other.name)
                .isEquals();
        } else {
            return false;
        }
    }

    public String getModule() {
        return module;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int hashCode() {
        return Objects.hash(module, name);
    }

    @Override
    public String toString() {
        return module + "#" + name;
    }
}
