package service.exceptions;

public class TaskLoadException extends RuntimeException {

    public TaskLoadException() {
        super();
    }

    public TaskLoadException(String message) {
        super(message);
    }

    public TaskLoadException(Throwable cause) {
        super(cause);
    }

    public TaskLoadException(String message, Throwable cause) {
        super(message, cause);
    }

}
