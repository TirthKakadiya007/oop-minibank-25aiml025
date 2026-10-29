package minibank.exception;

/**
 * Exception thrown when an account has insufficient funds to fulfill a withdrawal or transfer.
 * Carries the exact shortfall amount needed to complete the transaction.
 */
public class InsufficientFundsException extends BankException {
    private final long shortfall;

    public InsufficientFundsException(String message, long shortfall) {
        super(message);
        this.shortfall = shortfall;
    }

    public long getShortfall() {
        return shortfall;
    }
}
