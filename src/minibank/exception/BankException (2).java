package minibank.exception;

/**
 * Base checked exception class for all MiniBank application exceptions.
 */
public class BankException extends Exception {
    public BankException(String message) {
        super(message);
    }

    public BankException(String message, Throwable cause) {
        super(message, cause);
    }
}
