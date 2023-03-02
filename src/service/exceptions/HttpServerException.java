package service.exceptions;

public class HttpServerException extends RuntimeException {

    public HttpServerException() {
        super();
    }

    public HttpServerException(String message) {
        super(message);
    }

    public HttpServerException(Throwable cause) {
        super(cause);
    }

    public HttpServerException(String message, Throwable cause) {
        super(message, cause);
    }

}
