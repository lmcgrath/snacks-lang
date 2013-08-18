package snacks.lang.compiler;

import static org.apache.commons.lang.StringEscapeUtils.unescapeJava;
import static snacks.lang.compiler.Scanner.DETECT_FUNCTION_STATE;
import static snacks.lang.compiler.Scanner.EAT_NEWLINE_STATE;
import static snacks.lang.compiler.Scanner.SELECTOR_STATE;
import static snacks.lang.compiler.Scanner.YYINITIAL;
import static snacks.lang.compiler.Terminals.STRING;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Deque;
import beaver.Symbol;

public abstract class AbstractScanner extends beaver.Scanner implements AutoCloseable {

    private final Deque<Integer> braces = new ArrayDeque<>();
    private final Deque<Integer> states = new ArrayDeque<>();
    private final StringBuilder string = new StringBuilder();
    protected String source = "NULL";

    public AbstractScanner() {
        states.push(YYINITIAL);
    }

    public abstract int column();

    public abstract int length();

    public abstract int line();

    public abstract void yybegin(int state);

    public abstract String yytext();

    protected void badInput() throws IOException {
        String message = "Unexpected input '" + yytext() + "'";
        error(message);
        throw new IOException(message);
    }

    protected void beginInterpolation() {
        enterState(0);
        braces.push(0);
    }

    protected void beginString(int state) {
        enterState(state);
        string.setLength(0);
    }

    protected void bracesDown() {
        if (!braces.isEmpty()) {
            braces.push(braces.pop() - 1);
        }
    }

    protected void bracesUp() {
        if (braces.isEmpty()) {
            braces.push(1);
        } else {
            braces.push(braces.pop() + 1);
        }
    }

    protected void detectFunction() {
        enterState(DETECT_FUNCTION_STATE);
    }

    protected void detectNewLine() {
        enterState(EAT_NEWLINE_STATE);
    }

    protected void detectSelector() {
        enterState(SELECTOR_STATE);
    }

    protected boolean endOfInterpolation() {
        if (!braces.isEmpty() && braces.peek() < 0) {
            leaveState();
            braces.pop();
            return true;
        } else {
            return false;
        }
    }

    protected void enterState(int state) {
        states.push(state);
        yybegin(state);
    }

    protected void error(String message) {
        throw new ScannerException(message + " in " + source + " (" + line() + ", " + column() + ")");
    }

    protected void flipState(int state) {
        states.pop();
        enterState(state);
    }

    protected void gatherString() {
        string.append(yytext());
    }

    protected boolean hasString() {
        return string.length() > 0;
    }

    protected void leaveState() {
        states.pop();
        yybegin(states.peek());
    }

    protected void octalToHex() {
        string.append(octalToHex(yytext()));
    }

    protected String octalToHex(String text) {
        return String.format("\\u%04x", Integer.parseInt(text.substring(1), 8));
    }

    protected Symbol rawString() {
        String value = string.toString();
        string.setLength(0);
        return token(STRING, value);
    }

    protected Symbol string() {
        String value = string.toString();
        string.setLength(0);
        return token(STRING, unescapeJava(value.replaceAll("\\\\u+", "\\\\u")));
    }

    protected Symbol token(short type) {
        return new Token(type, line(), column(), length(), source);
    }

    protected Symbol token(short type, Object value) {
        return new Token(type, line(), column(), length(), source, value);
    }
}