package snacks.lang.compiler;

import static org.apache.commons.lang.StringUtils.join;

import java.io.ByteArrayInputStream;
import java.nio.charset.Charset;
import java.util.Set;
import beaver.Symbol;
import snacks.lang.compiler.Parser.AltGoals;
import snacks.lang.compiler.ast.AstNode;
import snacks.lang.compiler.ast.SymbolEnvironment;

public final class CompilerUtil {

    public static Symbol expression(String... inputs) {
        return new Parser().parse(
            new Scanner(new ByteArrayInputStream(join(inputs, "\n").getBytes(Charset.forName("UTF-8")))),
            AltGoals.expression
        );
    }

    public static Symbol parse(String... inputs) {
        return new Parser().parse(
            new Scanner(new ByteArrayInputStream(join(inputs, "\n").getBytes(Charset.forName("UTF-8"))))
        );
    }

    public static Set<AstNode> translate(String... inputs) {
        return translate(new SymbolEnvironment(), inputs);
    }

    public static Set<AstNode> translate(SymbolEnvironment environment, String... inputs) {
        return new Translator(environment, "test").translateModule(parse(inputs));
    }

    private CompilerUtil() {
        // intentionally empty
    }
}
