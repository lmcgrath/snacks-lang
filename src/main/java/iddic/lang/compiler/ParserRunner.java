package iddic.lang.compiler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.List;
import iddic.lang.IddicException;
import iddic.lang.cli.CommandLineRunner;
import iddic.lang.cli.RunnerException;

public class ParserRunner implements CommandLineRunner {

    private final BufferedReader input;
    private final PrintStream output;

    public ParserRunner() {
        this.input = new BufferedReader(new InputStreamReader(System.in));
        this.output = System.out;
    }

    @Override
    public String getCommand() {
        return "parse";
    }

    @Override
    public String getHelpText() {
        return "Parses input and displays the resultant concrete syntax tree";
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
                        output.println(new Parser(new CharStream(input)).parse());
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
