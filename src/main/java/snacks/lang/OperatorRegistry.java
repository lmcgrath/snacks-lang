package snacks.lang;

import java.util.HashMap;
import java.util.Map;

public class OperatorRegistry {

    private final Map<String, OpEntry> operators;

    public OperatorRegistry() {
        operators = new HashMap<>();
    }

    public Operator getOperator(String name) {
        OpEntry entry = operators.get(name);
        if (entry != null) {
            return entry.toOperator(name);
        } else {
            return null;
        }
    }

    public boolean isOperator(String name) {
        return operators.containsKey(name);
    }

    public void registerPrefix(int precedence, String name) {
        operators.put(name, new OpEntry(Fixity.RIGHT, precedence, 1));
    }

    public void registerInfix(int precedence, Fixity fixity, String name) {
        operators.put(name, new OpEntry(fixity, precedence, 2));
    }

    private static final class OpEntry {

        private final Fixity fixity;
        private final int precedence;
        private final int arity;

        public OpEntry(Fixity fixity, int precedence, int arity) {
            this.fixity = fixity;
            this.precedence = precedence;
            this.arity = arity;
        }

        public Operator toOperator(String name) {
            return new Operator(fixity, precedence, arity, name);
        }
    }
}
