package snacks.lang.compiler;

import static org.apache.commons.lang.StringUtils.join;

import java.io.ByteArrayInputStream;
import java.nio.charset.Charset;
import java.util.Set;
import beaver.Symbol;
import snacks.lang.SnacksException;
import snacks.lang.compiler.Parser.AltGoals;
import snacks.lang.compiler.ast.AstNode;

final class CompilerUtil {

    public static Symbol expression(String... inputs) {
        try {
            return new Parser().parse(
                new Scanner(new ByteArrayInputStream(join(inputs, "\n").getBytes(Charset.forName("UTF-8")))),
                AltGoals.expression
            );
        } catch (SnacksException exception) {
            throw new RuntimeException(exception);
        }
    }

    public static Symbol parse(String... inputs) {
        try {
            return new Parser().parse(
                new Scanner(new ByteArrayInputStream(join(inputs, "\n").getBytes(Charset.forName("UTF-8"))))
            );
        } catch (SnacksException exception) {
            throw new RuntimeException(exception);
        }
    }

    public static Set<AstNode> translate(String... inputs) throws SnacksException {
        return translate(new SymbolEnvironment(), inputs);
    }

    public static Set<AstNode> translate(SymbolEnvironment environment, String... inputs) throws SnacksException {
        return new Translator(environment, "test").translateModule(parse(inputs));
    }

    private CompilerUtil() {
        // intentionally empty
    }
}
