package snacks.lang.compiler;

import static java.lang.System.err;
import static java.lang.System.out;

import java.io.*;
import java.nio.charset.Charset;
import java.util.List;
import snacks.lang.SnacksException;
import snacks.lang.cli.CommandLineRunner;

public class ParserRunner implements CommandLineRunner {

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
        PrintStream systemError = err;
        System.setErr(out);
        out.println("Type any input to see how it parses:");
        out.print(">>> ");
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        Parser parser = new Parser();
        SyntaxPrinter printer = new SyntaxPrinter(out);
        String line;
        try {
            while (null != (line = reader.readLine())) {
                try {
                    if ("quit".equals(line)) {
                        break;
                    } else {
                        printer.print(parser.parse(
                            new Scanner(new ByteArrayInputStream(line.getBytes(Charset.forName("UTF-8"))))
                        ));
                    }
                } catch (SnacksException | ScannerException exception) {
                    exception.printStackTrace(out);
                }
                out.print(">>> ");
            }
            out.println("Goodbye!");
        } catch (IOException exception) {
            exception.printStackTrace(out);
        } finally {
            System.setErr(systemError);
        }
    }
}
