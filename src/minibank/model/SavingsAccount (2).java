package minibank.model;

/**
 * SavingsAccount subclass of Account (Practical 5).
 * Maintains a minimum balance rule and offers 4.0% annual interest.
 */
public class SavingsAccount extends Account {
    private static final long serialVersionUID = 1L;

    private long minBalance;

    public SavingsAccount(String ownerName, long balance, long minBalance) {
        super(ownerName, balance);
        this.minBalance = minBalance;
    }

    public SavingsAccount(String ownerName, long balance) {
        this(ownerName, balance, 1000L); // Default minimum balance 1000 INR
    }

    public long getMinBalance() {
        return minBalance;
    }

    public void setMinBalance(long minBalance) {
        this.minBalance = minBalance;
    }

    @Override
    public double interestRate() {
        return 4.0;
    }

    @Override
    public boolean canWithdraw(long amount) {
        return (getBalance() - amount) >= minBalance;
    }

    @Override
    public String toString() {
        return super.toString() + " [SavingsAccount | MinBal: " + minBalance + " INR]";
    }
}
