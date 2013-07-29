package iddic.lang.cli;

public class RunnerException extends RuntimeException {

    private static final long serialVersionUID = -2857846745932591435L;

    public RunnerException(String message) {
        super(message);
    }

    public RunnerException(Throwable cause) {
        super(cause);
    }
}
