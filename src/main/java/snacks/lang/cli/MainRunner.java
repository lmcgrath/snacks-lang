package snacks.lang.cli;

import static java.lang.Math.max;
import static java.util.Arrays.asList;

import java.io.*;
import java.util.*;
import java.util.Map.Entry;

public final class MainRunner {

    private static final String HELP = "help";
    private static final String RUN = "run";

    public static void main(String[] args)  {
        new MainRunner().run(new LinkedList<>(asList(args)));
    }

    private final Iterable<CommandLineRunner> runners;
    private final PrintStream out;
    private final CommandLineRunner helpRunner;

    public MainRunner() {
        this.out = System.out;
        this.runners = ServiceLoader.load(CommandLineRunner.class);
        this.helpRunner = new HelpRunner();
    }

    public void run(List<String> args) {
        if (args.isEmpty()) {
            requireCommand();
            helpRunner.run(args);
        } else {
            runCommand(args);
        }
    }

    private CommandLineRunner getRunnerByArguments(List<String> args) {
        CommandLineRunner runner = getRunnerByCommand(args.get(0));
        if (runner == null && HELP.equals(args.get(0))) {
            return helpRunner;
        } else {
            return runner;
        }
    }

    private CommandLineRunner getRunnerByCommand(String name) {
        for (CommandLineRunner runner : runners) {
            if (name.equals(runner.getCommand())) {
                return runner;
            }
        }
        return null;
    }

    private void requireCommand() {
        out.println("No command specified.");
        out.println();
    }

    private void runCommand(List<String> args) {
        CommandLineRunner runner = getRunnerByArguments(args);
        if (runner == null) {
            runner = getRunnerByCommand(RUN);
            if (runner == null) {
                helpRunner.run(args);
            } else {
                runner.run(args);
            }
        } else {
            runner.run(args.subList(1, args.size()));
        }
    }

    private final class HelpRunner implements CommandLineRunner {

        @Override
        public String getCommand() {
            return HELP;
        }

        @Override
        public String getHelpText() {
            return "Displays this help message";
        }

        @Override
        public void run(List<String> args) {
            if (args.size() > 0) {
                CommandLineRunner runner = getRunnerByCommand(args.get(0));
                if (runner == null) {
                    out.println("No command found for '" + args.get(0) + "'");
                } else {
                    printDoc(runner);
                }
            } else {
                displayHelp();
            }
        }

        private void displayHelp() {
            out.println("Available commands:");
            out.println();
            Map<String, String> helps = new HashMap<>();
            int length = processHelps(helps);
            for (Entry<String, String> entry : helps.entrySet()) {
                String command = pad(entry.getKey() + ":", length + 1);
                String helpText = entry.getValue();
                out.println(command + " \t" + helpText);
            }
            out.println();
            out.println("For additional information about a specific command, type 'help' and then the");
            out.println("command name for more details.");
            out.println();
        }

        private String pad(String text, int length) {
            return String.format("%1$" + length + "s", text);
        }

        private void printDoc(CommandLineRunner runner) {
            Class<?> clazz = runner.getClass();
            try (InputStream docStream = clazz.getResourceAsStream('/' + clazz.getName().replace('.', '/') + ".txt")) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(docStream));
                String line = reader.readLine();
                while (line != null) {
                    out.println(line);
                    line = reader.readLine();
                }
                out.println();
            } catch (IOException exception) {
                throw new RunnerException(exception);
            }
        }

        private int processHelps(Map<String, String> helps) {
            int length = 0;
            for (CommandLineRunner runner : runners) {
                String command = runner.getCommand();
                length = max(length, command.length());
                helps.put(command, runner.getHelpText());
            }
            return length;
        }
    }
}
