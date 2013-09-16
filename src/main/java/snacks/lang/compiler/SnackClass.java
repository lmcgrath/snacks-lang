package snacks.lang.compiler;

import me.qmx.jitescript.JiteClass;
import snacks.lang.Type;

class SnackClass {

    private final String module;
    private final String name;
    private final JiteClass jiteClass;
    private final Type type;

    public SnackClass(String module, String name, Type type, JiteClass jiteClass) {
        this.module = module;
        this.name = name;
        this.jiteClass = jiteClass;
        this.type = type;
    }

    public JiteClass getJiteClass() {
        return jiteClass;
    }

    public String getModule() {
        return module;
    }

    public String getName() {
        return name;
    }

    public Type getType() {
        return type;
    }
}
