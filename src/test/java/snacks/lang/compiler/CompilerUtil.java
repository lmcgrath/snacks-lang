package snacks.lang.compiler;

import static org.apache.commons.lang.StringUtils.join;

import java.io.ByteArrayInputStream;
import java.nio.charset.Charset;
import beaver.Symbol;
import snacks.lang.SnacksException;
import snacks.lang.compiler.Parser.AltGoals;

final class CompilerUtil {

    static Symbol expression(String... inputs) {
        try {
            return new Parser().parse(
                new Scanner(new ByteArrayInputStream(join(inputs, "\n").getBytes(Charset.forName("UTF-8")))),
                AltGoals.expression
            );
        } catch (SnacksException exception) {
            throw new RuntimeException(exception);
        }
    }

    static Symbol parse(String... inputs) {
        try {
            return new Parser().parse(
                new Scanner(new ByteArrayInputStream(join(inputs, "\n").getBytes(Charset.forName("UTF-8"))))
            );
        } catch (SnacksException exception) {
            throw new RuntimeException(exception);
        }
    }

    private CompilerUtil() {
        // intentionally empty
    }
}
