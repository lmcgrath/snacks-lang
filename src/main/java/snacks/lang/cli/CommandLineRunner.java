package snacks.lang.cli;

import java.util.List;

public interface CommandLineRunner {

    String getCommand();

    String getHelpText();

    void run(List<String> args);
}
