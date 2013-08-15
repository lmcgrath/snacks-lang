package snacks.lang.compiler;

import static java.lang.System.err;
import static java.lang.System.out;

import java.io.*;
import java.nio.charset.Charset;
import java.util.List;
import snacks.lang.cli.CommandLineRunner;

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
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String line;
        try {
            while (null != (line = reader.readLine())) {
                try {
                    if ("quit".equals(line)) {
                        break;
                    } else {
                        out.println(new Parser().parse(
                            new Scanner(new ByteArrayInputStream(line.getBytes(Charset.forName("UTF-8"))))
                        ));
                    }
                } catch (Parser.Exception | IOException | ScannerException exception) {
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
