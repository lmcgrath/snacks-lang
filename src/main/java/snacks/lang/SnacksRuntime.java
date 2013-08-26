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

    public static final Handle APPLY_BOOTSTRAP = new Handle(
        Opcodes.H_INVOKESTATIC,
        p(SnacksRuntime.class),
        "reference",
        sig(CallSite.class, Lookup.class, String.class, MethodType.class)
    );

    public static final Handle REFERENCE_BOOTSTRAP = new Handle(
        Opcodes.H_INVOKESTATIC,
        p(SnacksRuntime.class),
        "reference",
        sig(CallSite.class, Lookup.class, String.class, MethodType.class)
    );

    public static CallSite apply(Lookup lookup, String name, MethodType type) throws ReflectiveOperationException {
        MutableCallSite callSite = new MutableCallSite(type);
        MethodHandle send = Binder.from(type)
            .insert(0)
            .invokeStatic(lookup, SnacksRuntime.class, name);
        callSite.setTarget(send);
        return callSite;
    }

    public static CallSite reference(Lookup lookup, String name, MethodType type) throws ReflectiveOperationException {
        MutableCallSite callSite = new MutableCallSite(type);
        MethodHandle send = Binder.from(type)
            .insert(0)
            .invokeStatic(lookup, SnacksRuntime.class, name);
        callSite.setTarget(send);
        return callSite;
    }

    public static Object apply(Object function, Object argument) throws Exception {
        ((Say) function).apply(argument.toString());
        return null;
    }

    public static Object reference(String module, String name) throws Exception {
        return new Say();
    }
}
