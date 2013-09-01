package snacks.lang.util;

import java.io.PrintStream;

public class PrinterState {

    private final PrintStream out;
    private int currentIndent;

    public PrinterState(PrintStream out) {
        this.out = out;
    }

    public void begin(Object node) {
        out.print(getIndent());
        out.println("+" + formatNode(node));
        indent();
    }

    public void end() {
        dedent();
    }

    public void print(Throwable exception) {
        exception.printStackTrace(out);
    }

    public void println(Object value) {
        out.print(getIndent());
        out.println(" " + value);
    }

    private void dedent() {
        currentIndent--;
    }

    private String getIndent() {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < currentIndent; i++) {
            builder.append("  ");
        }
        return builder.toString();
    }

    private void indent() {
        currentIndent++;
    }

    protected String formatNode(Object node) {
        return node.getClass().getSimpleName();
    }
}
