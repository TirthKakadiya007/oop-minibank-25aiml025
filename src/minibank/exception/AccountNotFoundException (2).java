package minibank.exception;

/**
 * Exception thrown when an operation specifies an account number that does not exist in the bank.
 */
public class AccountNotFoundException extends BankException {
    public AccountNotFoundException(String message) {
        super(message);
    }
}
