package minibank.exception;

/**
 * Exception thrown when a withdrawal exceeds the daily allowable threshold.
 */
public class DailyLimitExceededException extends BankException {
    public DailyLimitExceededException(String message) {
        super(message);
    }
}
