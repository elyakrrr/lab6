package shared.exceptions;

import java.io.Serializable;
import java.io.Serial;

public class InvalidDataException extends Exception implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    public InvalidDataException(String message) {
        super(message);
    }

    public InvalidDataException(String message, Throwable cause) {
        super(message, cause);
    }
}
