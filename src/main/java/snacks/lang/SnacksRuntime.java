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

@SuppressWarnings("unused")
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
            .insert(0)
            .invokeStatic(lookup, SnacksRuntime.class, name);
        callSite.setTarget(send);
        return callSite;
    }

    public static Object apply(Object function, Object argument) {
        ((say) function).apply(argument.toString());
        return null;
    }

    public static Object invoke(Invokable invokable) throws ReflectiveOperationException {
        return invokable.invoke();
    }

    public static Object reference(String module, String name) throws ReflectiveOperationException {
        if ("say".equals(name)) {
            return new say();
        } else {
            return SnacksRuntime.class.getClassLoader().loadClass(name).newInstance();
        }
    }
}
