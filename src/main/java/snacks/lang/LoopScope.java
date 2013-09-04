package snacks.lang;

import static me.qmx.jitescript.util.CodegenUtils.p;
import static me.qmx.jitescript.util.CodegenUtils.sig;

import me.qmx.jitescript.CodeBlock;
import org.objectweb.asm.tree.LabelNode;

final class LoopScope {

    private final CodeBlock block;
    private final LabelNode start;
    private final LabelNode end;

    public LoopScope(CodeBlock block) {
        this.block = block;
        this.start = new LabelNode();
        this.end = new LabelNode();
    }

    public void begin() {
        block.label(start);
    }

    public void end() {
        block.pop();
        block.go_to(start);
        block.label(end);
        block.aconst_null();
    }

    public void exit() {
        block.go_to(end);
        block.aconst_null();
    }

    public void next() {
        block.go_to(start);
        block.aconst_null();
    }

    public void testCondition() {
        block.ldc(true);
        block.invokestatic(p(Boolean.class), "valueOf", sig(Boolean.class, boolean.class));
        block.if_acmpne(end);
    }
}
