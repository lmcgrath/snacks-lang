package snacks.lang.ast;

import java.util.Objects;
import org.apache.commons.lang.builder.EqualsBuilder;
import snacks.lang.Type;

public class DeclaredExpression extends AstNode {

    private final String module;
    private final String name;
    private final AstNode body;
    private Type type;

    public DeclaredExpression(String module, String name, AstNode body) {
        this.module = module;
        this.name = name;
        this.body = body;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (o instanceof DeclaredExpression) {
            DeclaredExpression other = (DeclaredExpression) o;
            return new EqualsBuilder()
                .append(module, other.module)
                .append(name, other.name)
                .append(body, other.body)
                .append(type, other.type)
                .isEquals();
        } else {
            return false;
        }
    }

    public AstNode getBody() {
        return body;
    }

    public String getModule() {
        return module;
    }

    public String getName() {
        return name;
    }

    @Override
    public void print(AstPrinter printer) {
        printer.printDeclaredExpression(this);
    }

    @Override
    public void generate(Generator generator) {
        generator.generateDeclaredExpression(this);
    }

    @Override
    public Type getType() {
        if (type == null) {
            return body.getType();
        } else {
            return type;
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(module, name, body);
    }

    public void setType(Type type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "(" + module + "#" + name + " = " + body + ")";
    }
}
