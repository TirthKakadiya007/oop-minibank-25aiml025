package minibank.model;

/**
 * Functional interface for custom withdrawal rule validation.
 */
@FunctionalInterface
public interface WithdrawRule {
    boolean allow(Account account, long amount);
}
