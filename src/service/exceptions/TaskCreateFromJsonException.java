package service.exceptions;

public class TaskCreateFromJsonException extends RuntimeException {

    public TaskCreateFromJsonException() {
        super();
    }

    public TaskCreateFromJsonException(String message) {
        super(message);
    }

    public TaskCreateFromJsonException(Throwable cause) {
        super(cause);
    }

    public TaskCreateFromJsonException(String message, Throwable cause) {
        super(message, cause);
    }
}
