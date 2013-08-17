package snacks.lang.util;

import java.io.PrintStream;

public class PrinterState {

    private final PrintStream out;
    private int currentIndent;

    public PrinterState(PrintStream out) {
        this(out, 0);
    }

    public PrinterState(PrintStream out, int indent) {
        this.out = out;
        this.currentIndent = indent;
    }

    public void begin(Object node) {
        println(formatNode(node));
        indent();
    }

    public void end() {
        dedent();
    }

    public void println(Object value) {
        out.print(getIndent());
        out.println("+" + value);
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
