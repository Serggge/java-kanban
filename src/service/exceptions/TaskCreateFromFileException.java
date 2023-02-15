package service.exceptions;

public class TaskCreateFromFileException extends RuntimeException {

    public TaskCreateFromFileException() {
        super();
    }

    public TaskCreateFromFileException(String message) {
        super(message);
    }

    public TaskCreateFromFileException(Throwable cause) {
        super(cause);
    }

    public TaskCreateFromFileException(String message, Throwable cause) {
        super(message, cause);
    }

}
