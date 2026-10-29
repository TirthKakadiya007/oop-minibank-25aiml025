package minibank.model;

/**
 * CurrentAccount subclass of Account (Practical 5).
 * Allows negative balances down to -overdraftLimit; offers 0.0% interest.
 */
public class CurrentAccount extends Account {
    private static final long serialVersionUID = 1L;

    private long overdraftLimit;

    public CurrentAccount(String ownerName, long balance, long overdraftLimit) {
        super(ownerName, balance);
        this.overdraftLimit = overdraftLimit;
    }

    public CurrentAccount(String ownerName, long balance) {
        this(ownerName, balance, 5000L); // Default overdraft limit 5000 INR
    }

    public long getOverdraftLimit() {
        return overdraftLimit;
    }

    public void setOverdraftLimit(long overdraftLimit) {
        this.overdraftLimit = overdraftLimit;
    }

    @Override
    public double interestRate() {
        return 0.0;
    }

    @Override
    public boolean canWithdraw(long amount) {
        return (getBalance() - amount) >= -overdraftLimit;
    }

    @Override
    public String toString() {
        return super.toString() + " [CurrentAccount | Overdraft: " + overdraftLimit + " INR]";
    }
}
