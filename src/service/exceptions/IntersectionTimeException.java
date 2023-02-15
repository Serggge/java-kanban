package service.exceptions;

public class IntersectionTimeException extends RuntimeException {

    public IntersectionTimeException() {
        super();
    }

    public IntersectionTimeException(String message) {
        super(message);
    }

    public IntersectionTimeException(Throwable cause) {
        super(cause);
    }

    public IntersectionTimeException(String message, Throwable cause) {
        super(message, cause);
    }

}
