package snacks.lang;

import static snacks.lang.Fixity.RIGHT;

import java.util.HashMap;
import java.util.Map;
import snacks.lang.parser.syntax.Operator;

public class OperatorRegistry {

    private final Map<String, OpEntry> operators;

    public OperatorRegistry() {
        operators = new HashMap<>();
        operators.put("=", new OpEntry(RIGHT, 0));
    }

    public Operator getOperator(String name) {
        OpEntry entry = operators.get(name);
        if (entry != null) {
            return new Operator(entry.getFixity(), entry.getPrecedence(), name);
        } else {
            return null;
        }
    }

    public int getPrecedence(String name) {
        if (isOperator(name)) {
            return getOperator(name).getPrecedence();
        } else {
            return -1;
        }
    }

    public boolean isOperator(String name) {
        return operators.containsKey(name);
    }

    public boolean isNextOperator(String name, int minimum) {
        return isOperator(name) && getOperator(name).getPrecedence() >= minimum;
    }

    public boolean isRightOperator(String name, int precedence) {
        if (isOperator(name)) {
            Operator operator = getOperator(name);
            return operator.getPrecedence() > precedence
                || operator.getFixity() == RIGHT && operator.getPrecedence() == precedence;
        } else {
            return false;
        }
    }

    public void register(int precedence, Fixity fixity, String... names) {
        for (String name : names) {
            operators.put(name, new OpEntry(fixity, precedence));
        }
    }

    private static final class OpEntry {

        private final Fixity fixity;
        private final int precedence;

        public OpEntry(Fixity fixity, int precedence) {
            this.fixity = fixity;
            this.precedence = precedence;
        }

        public Fixity getFixity() {
            return fixity;
        }

        public int getPrecedence() {
            return precedence;
        }
    }
}
