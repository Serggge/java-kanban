package service.exceptions;

public class ServerResponseException extends RuntimeException {

    public ServerResponseException() {
        super();
    }

    public ServerResponseException(String message) {
        super(message);
    }

    public ServerResponseException(Throwable cause) {
        super(cause);
    }

    public ServerResponseException(String message, Throwable cause) {
        super(message, cause);
    }
}
