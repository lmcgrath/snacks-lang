package snacks.lang.ast;

import static org.apache.commons.lang.StringEscapeUtils.escapeJava;
import static snacks.lang.type.Types.STRING_TYPE;

import java.util.Objects;
import snacks.lang.type.Type;

public class StringConstant extends AstNode {

    private final String value;

    public StringConstant(String value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        return o == this || o instanceof StringConstant && Objects.equals(value, ((StringConstant) o).value);
    }

    @Override
    public void print(AstPrinter printer) {
        printer.printStringConstant(this);
    }

    @Override
    public void generate(Generator generator) {
        generator.generateStringConstant(this);
    }

    @Override
    public Type getType() {
        return STRING_TYPE;
    }

    public String getValue() {
        return value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return '"' + escapeJava(value) + '"';
    }
}
