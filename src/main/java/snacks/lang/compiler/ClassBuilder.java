package snacks.lang.compiler;

import java.util.*;
import me.qmx.jitescript.CodeBlock;
import me.qmx.jitescript.JiteClass;
import snacks.lang.ast.Exceptional;
import snacks.lang.ast.Generator;

class ClassBuilder {

    private final JiteClass jiteClass;
    private final List<String> fields;
    private final Deque<BlockScope> scopes;

    public ClassBuilder(JiteClass jiteClass) {
        this.jiteClass = jiteClass;
        this.fields = new ArrayList<>();
        this.scopes = new ArrayDeque<>();
    }

    public CodeBlock acceptBlock() {
        return scopes.pop().getBlock();
    }

    public CodeBlock beginBlock() {
        scopes.push(new BlockScope(new CodeBlock()));
        return block();
    }

    public CodeBlock block() {
        return scope().getBlock();
    }

    public void enterGuard() {
        scope().enterGuard();
    }

    public EmbraceScope currentEmbrace() {
        return scope().currentEmbrace();
    }

    public LoopScope currentLoop() {
        return scope().currentLoop();
    }

    public void enterEmbrace(int exceptionVar, Exceptional node) {
        scope().enterEmbrace(exceptionVar, node);
    }

    public LoopScope enterLoop() {
        return scopes.peek().enterLoop();
    }

    public void exitGuard() {
        scope().exitGuard();
    }

    public JiteClass getJiteClass() {
        return jiteClass;
    }

    public int getVariable(String name) {
        return scope().getVariable(name);
    }

    public boolean isField(String name) {
        return fields.contains(name);
    }

    public boolean isVariable(String name) {
        return scope().isVariable(name);
    }

    public void leaveEmbrace(Generator generator) {
        scope().leaveEmbrace(generator);
    }

    public void leaveGuard() {
        scope().leaveGuard();
    }

    public void leaveLoop() {
        scope().leaveLoop();
    }

    public void setFields(Collection<String> fields) {
        this.fields.clear();
        this.fields.addAll(fields);
    }

    private BlockScope scope() {
        return scopes.peek();
    }
}
