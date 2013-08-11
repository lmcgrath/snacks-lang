package iddic.lang.compiler;

import static java.lang.System.err;
import static java.lang.System.out;

import java.io.PrintStream;
import java.util.List;
import iddic.lang.IddicException;
import iddic.lang.cli.CommandLineRunner;
import iddic.lang.syntax.Expression;
import iddic.lang.syntax.Identifier;

public class ParserRunner implements CommandLineRunner {

    @Override
    public String getCommand() {
        return "parse";
    }

    @Override
    public String getHelpText() {
        return "Parses input and displays the resultant abstract syntax tree";
    }

    @Override
    public void run(List<String> args) {
        PrintStream systemError = err;
        System.setErr(out);
        out.println("Type any input to see how it parses:");
        out.print(">>> ");
        TextStream reader = new BufferedStream(System.in);
        Parser parser = new Parser(new TokenStream(new TokenSource(reader)));
        try {
            Expression quit = new Identifier("quit");
            while (true) {
                try {
                    Expression expression = parser.parse();
                    if (quit.equals(expression)) {
                        break;
                    } else {
                        out.println(expression);
                    }
                } catch (IddicException | ScanException exception) {
                    reader.consumeLine();
                    exception.printStackTrace(out);
                }
                out.print(">>> ");
            }
            out.println("Goodbye!");
        } finally {
            System.setErr(systemError);
        }
    }
}
