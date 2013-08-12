package iddic.lang.compiler.lexer;

public final class Terminals {

    public static final int
        LPAREN = 0,
        RPAREN = 1,
        DQUOTE = 2,
        STRING = 3,
        ID = 4,
        INT = 5,
        DOUBLE = 6,
        BOOL = 7,
        NOTHING = 8
        ;

    private static final String[] names = new String[] {
        "LPAREN",
        "RPAREN",
        "DQUOTE",
        "STRING",
        "ID",
        "INT",
        "DOUBLE",
        "BOOL",
        "NOTHING",
    };

    public static String nameOf(int kind) {
        return kind == -1 ? "end-of-file" : names[kind];
    }

    private Terminals() {
        // intentionally empty
    }
}
