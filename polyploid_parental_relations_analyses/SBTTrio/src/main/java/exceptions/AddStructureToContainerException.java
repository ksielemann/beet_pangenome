package exceptions;

import java.io.Serial;

/**
 * Exception thrown when trying to add a data structure to a container.
 */
public class AddStructureToContainerException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 42L;

    public AddStructureToContainerException(String message) {
        super(message);
    }

}
