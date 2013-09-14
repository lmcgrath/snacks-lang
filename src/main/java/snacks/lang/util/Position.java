package snacks.lang.util;

public class Position {

    private final String source;
    private final int startLine;
    private final int startColumn;
    private final int endLine;
    private final int endColumn;

    public Position(String source, int startLine, int startColumn) {
        this.source = source;
        this.startLine = startLine;
        this.startColumn = startColumn;
        this.endLine = -1;
        this.endColumn = -1;
    }

    public Position(Position start, int endLine, int endColumn) {
        this.source = start.source;
        this.startLine = start.startLine;
        this.startColumn = start.startColumn;
        this.endLine = endLine;
        this.endColumn = endColumn;
    }

    public int getEndColumn() {
        return endColumn;
    }

    public int getEndLine() {
        return endLine;
    }

    public int getStartColumn() {
        return startColumn;
    }

    public int getStartLine() {
        return startLine;
    }

    @Override
    public String toString() {
        if (endLine == -1) {
            return "'" + source + "' (" + startLine + "," + startColumn + ")";
        } else {
            return "'" + source + "' (" + startLine + "," + startColumn + "-" + endLine + "," + endColumn + ")";
        }
    }
}
