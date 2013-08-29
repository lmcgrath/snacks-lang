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

    public static final Handle BOOTSTRAP = new Handle(
        Opcodes.H_INVOKESTATIC,
        p(SnacksRuntime.class),
        "bootstrap",
        sig(CallSite.class, Lookup.class, String.class, MethodType.class)
    );

    public static CallSite bootstrap(Lookup lookup, String name, MethodType type) throws ReflectiveOperationException {
        MutableCallSite callSite = new MutableCallSite(type);
        MethodHandle send = Binder.from(type)
            .insert(0, lookup, callSite)
            .invokeStatic(lookup, SnacksRuntime.class, name);
        callSite.setTarget(send);
        return callSite;
    }

    public static Object apply(Lookup lookup, MutableCallSite callSite, Object function, Object argument) throws Throwable {
        MethodHandle target;
        target = Binder.from(Object.class, Object.class, Object.class)
            .cast(Object.class, function.getClass(), argument.getClass())
            .invokeVirtual(lookup, "apply");
        return target.invoke(function, argument);
    }
}
