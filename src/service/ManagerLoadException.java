package service;

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

    ManagerLoadException(String message, Throwable cause) {
        super(message, cause);
    }

}
