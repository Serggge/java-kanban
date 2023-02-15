package service.exceptions;

public class ManagerLoadException extends RuntimeException {

    public ManagerLoadException() {
        super();
    }

    public ManagerLoadException(String message) {
        super(message);
    }

    public ManagerLoadException(Throwable cause) {
        super(cause);
    }

    public ManagerLoadException(String message, Throwable cause) {
        super(message, cause);
    }

}
