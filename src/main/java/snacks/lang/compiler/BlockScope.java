package snacks.lang.compiler;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.LinkedHashMap;
import java.util.Map;
import me.qmx.jitescript.CodeBlock;
import snacks.lang.ast.Exceptional;
import snacks.lang.ast.Generator;

class BlockScope {

    private final CodeBlock block;
    private final Map<String, Integer> variables;
    private final Deque<GuardScope> guards;
    private final Deque<EmbraceScope> embraces;
    private final Deque<LoopScope> loops;

    public BlockScope(CodeBlock block) {
        this.block = block;
        this.variables = new LinkedHashMap<>();
        this.guards = new ArrayDeque<>();
        this.loops = new ArrayDeque<>();
        this.embraces = new ArrayDeque<>();
    }

    public EmbraceScope currentEmbrace() {
        return embraces.peek();
    }

    public LoopScope currentLoop() {
        return loops.peek();
    }

    public void enterEmbrace(int exceptionVar, Exceptional node) {
        embraces.push(new EmbraceScope(exceptionVar, block, node.getEnsure()));
    }

    public void enterGuard() {
        guards.push(new GuardScope(block));
    }

    public LoopScope enterLoop() {
        LoopScope loop = new LoopScope(block);
        loops.push(loop);
        loop.begin();
        return loop;
    }

    public void exitGuard() {
        guards.peek().exitGuard();
    }

    public CodeBlock getBlock() {
        return block;
    }

    public int getVariable(String name) {
        if (!variables.containsKey(name)) {
            variables.put(name, variables.size() + 1);
        }
        return variables.get(name);
    }

    public void leaveEmbrace(Generator generator) {
        EmbraceScope scope = embraces.pop();
        block.trycatch(scope.getStart(), scope.getEnd(), scope.getError(), null);
        scope.generateEnsureAll(generator);
        block.label(scope.getExit());
    }

    public void leaveGuard() {
        guards.pop().leaveGuard();
    }

    public void leaveLoop() {
        loops.pop().end();
    }
}
