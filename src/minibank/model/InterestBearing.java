package minibank.model;

/**
 * Interface representing accounts that earn interest.
 * Includes default calculation methods.
 */
public interface InterestBearing {
    /**
     * @return annual interest rate percentage (e.g. 4.0 for 4%).
     */
    double interestRate();

    /**
     * @return current balance of the account.
     */
    long getBalance();

    /**
     * Calculates estimated yearly interest based on current balance and interest rate.
     */
    default double yearlyInterest() {
        return getBalance() * (interestRate() / 100.0);
    }

    /**
     * Calculates estimated monthly interest (Supplementary practical requirement).
     */
    default double monthlyInterest() {
        return yearlyInterest() / 12.0;
    }
}
