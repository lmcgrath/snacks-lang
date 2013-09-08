package snacks.lang;

import static me.qmx.jitescript.util.CodegenUtils.p;
import static me.qmx.jitescript.util.CodegenUtils.sig;
import static org.apache.commons.lang.StringUtils.capitalize;
import static org.apache.commons.lang.reflect.MethodUtils.getMatchingAccessibleMethod;

import java.lang.invoke.CallSite;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.invoke.MethodType;
import java.lang.invoke.MutableCallSite;
import java.lang.reflect.Method;
import com.headius.invokebinder.Binder;
import org.objectweb.asm.Handle;
import org.objectweb.asm.Opcodes;

public class SnacksDispatcher {

    public static final Handle BOOTSTRAP_APPLY = new Handle(
        Opcodes.H_INVOKESTATIC,
        p(SnacksDispatcher.class),
        "bootstrap",
        sig(CallSite.class, Lookup.class, String.class, MethodType.class)
    );

    public static final Handle BOOTSTRAP_GET = new Handle(
        Opcodes.H_INVOKESTATIC,
        p(SnacksDispatcher.class),
        "bootstrap",
        sig(CallSite.class, Lookup.class, String.class, MethodType.class)
    );

    private static final String apply = "apply";

    public static CallSite bootstrap(Lookup lookup, String name, MethodType type) throws ReflectiveOperationException {
        MutableCallSite callSite = new MutableCallSite(type);
        MethodHandle send = Binder.from(type)
            .insert(0, lookup)
            .invokeStatic(lookup, SnacksDispatcher.class, name);
        callSite.setTarget(send);
        return callSite;
    }

    public static Object apply(Lookup lookup, Object function, Object argument) throws Throwable {
        Method method = methodFor(function, argument);
        return Binder.from(method.getReturnType(), function.getClass(), method.getParameterTypes()[0])
            .invokeVirtual(lookup, apply)
            .invoke(function, argument);
    }

    public static Object get(Lookup lookup, Object expression, String property) throws Throwable {
        String getter = "get" + capitalize(property);
        Method method = getMatchingAccessibleMethod(expression.getClass(), "get" + capitalize(property), new Class[0]);
        if (method == null) {
            throw new NoSuchMethodException(p(expression.getClass()) + ":" + getter + ":" + sig(Object.class));
        } else {
            return Binder.from(method.getReturnType(), expression.getClass())
                .invokeVirtual(lookup, getter)
                .invoke(expression);
        }
    }

    private static Method methodFor(Object function, Object argument) throws Throwable {
        Method method = getMatchingAccessibleMethod(function.getClass(), apply, new Class[] { argument.getClass() });
        if (method == null) {
            throw new NoSuchMethodException(p(function.getClass()) + ":" + apply + ":" + sig(Object.class, argument.getClass()));
        } else {
            return method;
        }
    }
}
