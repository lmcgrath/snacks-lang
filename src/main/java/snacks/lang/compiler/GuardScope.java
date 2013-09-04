package snacks.lang.compiler;

import me.qmx.jitescript.CodeBlock;
import org.objectweb.asm.tree.LabelNode;

class GuardScope {

    private final CodeBlock block;
    private final LabelNode exit;

    public GuardScope(CodeBlock block) {
        this.block = block;
        this.exit = new LabelNode();
    }

    public void exitGuard() {
        block.go_to(exit);
    }

    public void leaveGuard() {
        block.label(exit);
    }
}
