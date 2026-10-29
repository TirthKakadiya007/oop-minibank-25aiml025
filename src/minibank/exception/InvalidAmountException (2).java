package minibank.exception;

/**
 * Exception thrown when a monetary transaction amount is non-positive (<= 0) or improperly formatted.
 */
public class InvalidAmountException extends BankException {
    public InvalidAmountException(String message) {
        super(message);
    }
}
