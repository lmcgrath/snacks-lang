package snacks.lang.ast;

import java.util.Objects;
import org.apache.commons.lang.builder.EqualsBuilder;

public class Loop extends AstNode {

    private final AstNode condition;
    private final AstNode body;

    public Loop(AstNode condition, AstNode body) {
        this.condition = condition;
        this.body = body;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (o instanceof Loop) {
            Loop other = (Loop) o;
            return new EqualsBuilder()
                .append(condition, other.condition)
                .append(body, other.body)
                .isEquals();
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(condition, body);
    }

    @Override
    public void generate(Generator generator) {
        generator.generateLoop(this);
    }

    public AstNode getBody() {
        return body;
    }

    public AstNode getCondition() {
        return condition;
    }

    @Override
    public Type getType() {
        return body.getType();
    }

    @Override
    public String toString() {
        return "(while " + condition + " do " + body + ")";
    }
}
