package snacks.lang;

import java.security.ProtectionDomain;

public interface SnacksRegistry {

    Class<?> classOf(String qualifiedName);

    Class<?> defineSnack(SnackDefinition definition);

    Class<?> defineSnack(SnackDefinition definition, ProtectionDomain protectionDomain);

    Operator getOperator(String name);

    boolean isOperator(String name);

    Type typeOf(String qualifiedName);
}
