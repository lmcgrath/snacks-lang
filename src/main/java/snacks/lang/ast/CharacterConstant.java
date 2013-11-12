package snacks.lang.ast;

import static org.apache.commons.lang.StringEscapeUtils.escapeJava;
import static snacks.lang.Types.characterType;

import java.util.Objects;
import snacks.lang.Type;

public class CharacterConstant extends AstNode {

    private final char value;

    public CharacterConstant(char value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        return o == this || o instanceof CharacterConstant && value == ((CharacterConstant) o).value;
    }

    @Override
    public void generate(Generator generator) {
        generator.generateCharacterConstant(this);
    }

    @Override
    public Type getType() {
        return characterType();
    }

    public char getValue() {
        return value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public void print(AstPrinter printer) {
        printer.printCharacterConstant(this);
    }

    @Override
    public String toString() {
        return "'" + escapeJava(String.valueOf(value)) + "'";
    }
}
