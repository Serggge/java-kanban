package service;

public class TaskCreationException extends RuntimeException {

    public TaskCreationException() {
        super();
    }

    public TaskCreationException(String message) {
        super(message);
    }

    public TaskCreationException(Throwable cause) {
        super(cause);
    }

    TaskCreationException(String message, Throwable cause) {
        super(message, cause);
    }

}
