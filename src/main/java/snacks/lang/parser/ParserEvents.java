package snacks.lang.parser;

import static beaver.Symbol.getColumn;
import static beaver.Symbol.getLine;
import static java.util.logging.Logger.getLogger;
import static org.apache.commons.lang.StringEscapeUtils.escapeJava;
import static snacks.lang.parser.Terminals.NAMES;

import java.util.logging.Logger;
import beaver.Parser.Events;
import beaver.Symbol;

class ParserEvents extends Events {

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
        String value;
        if (symbol.value != null) {
            value = '"' + escapeJava(symbol.value.toString()) + "\" ";
        } else {
            value = "";
        }
        logger.warning(message + ' ' + value + getCompleteId(symbol) + ' ' + getPosition(symbol));
    }

    private String getPosition(Symbol symbol) {
        if (symbol instanceof Token) {
            Token token = (Token) symbol;
            return token.getPosition().toString();
        } else {
            return "(" + (getLine(symbol.getStart()) + 1) + "," + (getColumn(symbol.getStart()) + 1) + ")";
        }
    }

    private String getCompleteId(Symbol symbol) {
        return "(#" + symbol.getId() + ", " + NAMES[symbol.getId()] + ")";
    }
}
