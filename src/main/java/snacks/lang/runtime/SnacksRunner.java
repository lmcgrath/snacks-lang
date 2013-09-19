package snacks.lang.runtime;

import java.util.List;
import snacks.lang.Invokable;
import snacks.lang.cli.CommandLineRunner;
import snacks.lang.cli.RunnerException;

public class SnacksRunner implements CommandLineRunner {

    private final SnacksClassLoader loader;

    public SnacksRunner() {
        loader = new SnacksClassLoader(getClass().getClassLoader());
    }

    @Override
    public String getCommand() {
        return "run";
    }

    @Override
    public String getHelpText() {
        return "Run a snack";
    }

    @Override
    public void run(List<String> args) {
        String name = args.remove(0) + ".main";
        Class<?> clazz = loader.classOf(name);
        try {
            ((Invokable) clazz.newInstance()).invoke();
        } catch (ReflectiveOperationException exception) {
            throw new RunnerException(exception);
        }
    }
}
