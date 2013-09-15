package snacks.lang.parser;

import static java.lang.System.err;
import static java.lang.System.out;

import java.io.*;
import java.nio.charset.Charset;
import java.util.List;
import snacks.lang.SnacksLoader;
import snacks.lang.ast.AstPrinter;
import snacks.lang.cli.CommandLineRunner;

public class TranslatorRunner implements CommandLineRunner {

    @Override
    public String getCommand() {
        return "translate";
    }

    @Override
    public String getHelpText() {
        return "Parses input and displays the resultant abstract syntax tree";
    }

    @Override
    public void run(List<String> args) {
        PrintStream systemError = err;
        System.setErr(out);
        out.println("Type any input to see how it translates:");
        out.print(">>> ");
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        Parser parser = new Parser();
        Translator translator = new Translator(new SymbolEnvironment(new SnacksLoader()), "cli");
        AstPrinter printer = new AstPrinter(out);
        String line;
        try {
            while (null != (line = reader.readLine())) {
                try {
                    if ("quit".equals(line)) {
                        break;
                    } else {
                        printer.print(translator.translateModule(parser.parse(
                            new Scanner("Command Line", new ByteArrayInputStream(line.getBytes(Charset.forName("UTF-8"))))
                        )));
                    }
                } catch (ParseException exception) {
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
