package iddic.lang.compiler;

public final class Terminals {

    private static int nextId = 0;

    public static final int
        LPAREN = nextId++,
        RPAREN = nextId++,
        DOUBLE_QUOTE = nextId++,
        STRING = nextId++,
        ID = nextId++,
        INT = nextId++,
        DOUBLE = nextId++,
        BOOL = nextId++,
        NOTHING = nextId++
        ;

    private static final String[] names = new String[] {
        "LPAREN",
        "RPAREN",
        "DOUBLE_QUOTE",
        "STRING",
        "ID",
        "INT",
        "DOUBLE",
        "BOOL",
        "NOTHING"
    };

    public static String nameOf(int kind) {
        return names[kind];
    }

    private Terminals() {
        // intentionally empty
    }
}
