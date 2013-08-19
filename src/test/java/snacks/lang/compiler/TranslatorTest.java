package snacks.lang.compiler;

import static org.junit.Assert.assertThat;
import static snacks.lang.compiler.CompilerUtil.parse;
import static snacks.lang.compiler.TranslatorMatcher.defines;
import static snacks.lang.compiler.ast.AstFactory.apply;
import static snacks.lang.compiler.ast.AstFactory.constant;
import static snacks.lang.compiler.ast.AstFactory.declaration;
import static snacks.lang.compiler.ast.AstFactory.reference;
import static snacks.lang.compiler.ast.Type.INTEGER_TYPE;
import static snacks.lang.compiler.ast.Type.STRING_TYPE;
import static snacks.lang.compiler.ast.Type.func;
import static snacks.lang.compiler.ast.Type.type;

import java.util.Set;
import org.junit.Before;
import org.junit.Test;
import snacks.lang.SnacksException;
import snacks.lang.compiler.ast.AstNode;
import snacks.lang.compiler.ast.Type;

public class TranslatorTest {

    private Type intType;
    private Type stringType;
    private Registry registry;

    @Before
    public void setUp() {
        intType = INTEGER_TYPE;
        stringType = STRING_TYPE;
        registry = new Registry();
        defineIntPlusInt();
        defineIntPlusString();
    }

    @Test
    public void shouldTranslateDeclaration() throws SnacksException {
        Type type = func(intType, func(intType, intType));
        assertThat(translate("example = 2 + 2"), defines(declaration("test", "example", apply(
            apply(reference("+", type), constant(2)), constant(2)
        ))));
    }

    @Test
    public void shouldTranslateTwoPlusString() throws SnacksException {
        Set<AstNode> nodes = translate(
            "value = 'Hello, World!'",
            "example = 2 + value"
        );
        assertThat(nodes, defines(declaration("test", "value", constant("Hello, World!"))));
        assertThat(nodes, defines(declaration("test", "example", apply(
            apply(reference("+", func(intType, func(stringType, stringType))), constant(2)),
            reference("test", "value", stringType)
        ))));
    }

    @Test(expected = UndefinedReferenceException.class)
    public void shouldThrowException_whenOperatorUndefined() throws SnacksException {
        registry.add(reference("test", "oddball", type("Unknown")));
        translate("example = 2 + oddball");
    }

    private void defineIntPlusInt() {
        registry.add(reference("snacks/lang", "+", func(intType, func(intType, intType))));
    }

    private void defineIntPlusString() {
        registry.add(reference("snacks/lang", "+", func(intType, func(stringType, stringType))));
    }

    private Set<AstNode> translate(String... inputs) throws SnacksException {
        return new Translator(registry).translate("test", parse(inputs));
    }
}
