package snacks.lang.ast;

import static snacks.lang.SnackKind.EXPRESSION;

import java.util.Objects;
import org.apache.commons.lang.builder.EqualsBuilder;
import snacks.lang.SnackKind;
import snacks.lang.type.Type;
import snacks.lang.Operator;

public class DeclaredExpression extends NamedNode {

    private final String module;
    private final String name;
    private final AstNode body;
    private Operator operator;
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

    @Override
    public void generate(Generator generator) {
        generator.generateDeclaredExpression(this);
    }

    public AstNode getBody() {
        return body;
    }

    @Override
    public SnackKind getKind() {
        return EXPRESSION;
    }

    @Override
    public String getModule() {
        return module;
    }

    @Override
    public String getName() {
        return name;
    }

    public Operator getOperator() {
        return operator;
    }

    public void setOperator(Operator operator) {
        this.operator = operator;
    }

    @Override
    public Type getType() {
        if (type == null) {
            return body.getType();
        } else {
            return type;
        }
    }

    public void setType(Type type) {
        this.type = type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(module, name, body);
    }

    public boolean isOperator() {
        return operator != null;
    }

    @Override
    public void print(AstPrinter printer) {
        printer.printDeclaredExpression(this);
    }

    @Override
    public String toString() {
        return "(" + module + "#" + name + " = " + body + ")";
    }
}
