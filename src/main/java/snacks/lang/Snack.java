package snacks.lang;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static snacks.lang.SnackKind.EXPRESSION;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Documented
@Target(TYPE)
@Retention(RUNTIME)
public @interface Snack {

    SnackKind kind() default EXPRESSION;

    String name();

    String[] arguments() default { };
}
