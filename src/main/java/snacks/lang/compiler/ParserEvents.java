package snacks.lang.compiler;

import static beaver.Symbol.getColumn;
import static beaver.Symbol.getLine;
import static snacks.lang.compiler.Terminals.NAMES;
import static java.util.logging.Logger.getLogger;

import java.util.logging.Logger;
import beaver.Parser.Events;
import beaver.Symbol;

public class ParserEvents extends Events {

    private final Logger logger;

    public ParserEvents() {
        logger = getLogger("SYNTAX");
    }

    @Override
    public void syntaxError(Symbol token) {
        report(token, "Syntax Error: unexpected token");
    }

    @Override
    public void unexpectedTokenRemoved(Symbol token) {
        report(token, "Recovered: removed unexpected token");
    }

    @Override
    public void missingTokenInserted(Symbol token) {
        report(token, "Recovered: inserted missing token");
    }

    @Override
    public void misspelledTokenReplaced(Symbol token) {
        report(token, "Recovered: replaced unexpected token with");
    }

    @Override
    public void errorPhraseRemoved(Symbol error) {
        report(error, "Recovered: removed error phrase");
    }

    private void report(Symbol symbol, String message) {
        StringBuilder builder = new StringBuilder()
            .append(getPosition(symbol))
            .append(": ").append(message).append(' ');
        if (symbol.value != null) {
            builder.append('"').append(symbol.value).append("\" ");
        }
        builder.append(getCompleteId(symbol));
        logger.warning(builder.toString());
    }

    private String getPosition(Symbol symbol) {
        return "(" + (getLine(symbol.getStart()) + 1) + ", " + (getColumn(symbol.getStart()) + 1) + ")";
    }

    private String getCompleteId(Symbol symbol) {
        return "(#" + symbol.getId() + ", " + NAMES[symbol.getId()] + ")";
    }
}
