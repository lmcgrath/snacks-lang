package snacks.lang;

import me.qmx.jitescript.CodeBlock;
import org.objectweb.asm.tree.LabelNode;
import snacks.lang.ast.AstNode;
import snacks.lang.ast.Generator;

class EmbraceScope {

    private final int exceptionVar;
    private final CodeBlock block;
    private final AstNode ensure;
    private final LabelNode start;
    private final LabelNode end;
    private final LabelNode exit;
    private final LabelNode error;

    public EmbraceScope(int exceptionVar, CodeBlock block, AstNode ensure) {
        this.exceptionVar = exceptionVar;
        this.block = block;
        this.ensure = ensure;
        this.start = new LabelNode();
        this.end = new LabelNode();
        this.exit = new LabelNode();
        this.error = new LabelNode();
    }

    public void generateEnsure(Generator generator) {
        if (ensure != null) {
            generator.generate(ensure);
        }
    }

    public void generateEnsureAll(Generator generator) {
        block.label(error);
        block.astore(exceptionVar);
        if (ensure != null) {
            LabelNode ensureLabel = new LabelNode();
            block.trycatch(error, ensureLabel, error, null);
            block.label(ensureLabel);
            generateEnsure(generator);
        }
        block.aload(exceptionVar);
        block.athrow();
    }

    public LabelNode getEnd() {
        return end;
    }

    public AstNode getEnsure() {
        return ensure;
    }

    public LabelNode getError() {
        return error;
    }

    public LabelNode getExit() {
        return exit;
    }

    public LabelNode getStart() {
        return start;
    }
}
