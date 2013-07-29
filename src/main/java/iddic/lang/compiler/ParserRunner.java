package iddic.lang.compiler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import iddic.lang.IddicException;
import iddic.lang.cli.CommandLineRunner;
import iddic.lang.cli.RunnerException;
import iddic.lang.compiler.syntax.SyntaxPrinter;

public class ParserRunner implements CommandLineRunner {

    private final BufferedReader input;
    private final PrintStream output;
    private final SyntaxPrinter printer;

    public ParserRunner() {
        this.input = new BufferedReader(new InputStreamReader(System.in));
        this.output = System.out;
        this.printer = new SyntaxPrinter();
    }

    @Override
    public String getCommand() {
        return "parse";
    }

    @Override
    public String getHelpText() {
        return "Parses input and displays the resultant concrete syntax tree";
    }

    public void run() {
        run(new ArrayList<String>());
    }

    @Override
    public void run(List<String> args) {
        PrintStream systemError = System.err;
        System.setErr(output);
        output.println("Type any input to see how it parses:");
        output.print(">>> ");
        try {
            String input;
            while (null != (input = this.input.readLine())) {
                if ("quit".equals(input)) {
                    break;
                } else if (!"".equals(input.trim())) {
                    try {
                        Translator translator = new Translator();
                        IddicLexer lexer = new IddicLexer(new ANTLRInputStream(input));
                        IddicParser parser = new IddicParser(new CommonTokenStream(lexer));
                        printer.print(translator.translate(parser.moduleDeclaration()), output);
                    } catch (IddicException exception) {
                        exception.printStackTrace(output);
                    }
                }
                output.print(">>> ");
            }
            output.println("Goodbye!");
        } catch (IOException exception) {
            throw new RunnerException(exception);
        } finally {
            System.setErr(systemError);
        }
    }
}
