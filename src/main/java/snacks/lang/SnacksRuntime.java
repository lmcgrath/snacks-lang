package snacks.lang;

import static me.qmx.jitescript.util.CodegenUtils.p;
import static me.qmx.jitescript.util.CodegenUtils.sig;

import java.lang.invoke.CallSite;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.invoke.MethodType;
import java.lang.invoke.MutableCallSite;
import com.headius.invokebinder.Binder;
import org.objectweb.asm.Handle;
import org.objectweb.asm.Opcodes;

public class SnacksRuntime {

    public static final Handle REFERENCE_BOOTSTRAP = new Handle(
        Opcodes.H_INVOKESTATIC,
        p(SnacksRuntime.class),
        "reference",
        sig(CallSite.class, Lookup.class, String.class, MethodType.class)
    );

    public static CallSite reference(Lookup lookup, String name, MethodType type) throws ReflectiveOperationException {
        MutableCallSite callSite = new MutableCallSite(type);
        MethodHandle send = Binder.from(type)
            .insert(0)
            .invokeStatic(lookup, SnacksRuntime.class, name);
        callSite.setTarget(send);
        return callSite;
    }

    public static Expression reference(String module, String name) throws Exception {
        return new Expression() {
            @Override
            public Expression apply(Expression argument) {
                System.out.println(((SnacksString) argument).getValue());
                return argument;
            }
        };
    }
}