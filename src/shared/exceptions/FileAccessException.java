package shared.exceptions;

import java.io.Serializable;
import java.io.Serial;

public class FileAccessException extends Exception implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    public FileAccessException(String message) {
        super(message);
    }

    public FileAccessException(String message, Throwable cause) {
        super(message, cause);
    }
}
