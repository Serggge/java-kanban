package service.exceptions;

public class EndpointException extends RuntimeException {

    public EndpointException() {
        super();
    }

    public EndpointException(String message) {
        super(message);
    }

    public EndpointException(Throwable cause) {
        super(cause);
    }

    public EndpointException(String message, Throwable cause) {
        super(message, cause);
    }
}
