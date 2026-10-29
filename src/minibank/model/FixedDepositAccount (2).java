package minibank.model;

/**
 * FixedDepositAccount subclass of Account (Practical 5).
 * Deposits are locked until maturity; offers 7.0% annual interest rate.
 */
public class FixedDepositAccount extends Account {
    private static final long serialVersionUID = 1L;

    private boolean matured;

    public FixedDepositAccount(String ownerName, long balance) {
        super(ownerName, balance);
        this.matured = false;
    }

    public FixedDepositAccount(String ownerName, long balance, boolean matured) {
        super(ownerName, balance);
        this.matured = matured;
    }

    public boolean isMatured() {
        return matured;
    }

    public void setMatured(boolean matured) {
        this.matured = matured;
    }

    @Override
    public double interestRate() {
        return 7.0;
    }

    /**
     * By default returns false (locked deposit), unless matured is marked true (Supplementary).
     */
    @Override
    public boolean canWithdraw(long amount) {
        if (!matured) {
            return false;
        }
        return getBalance() >= amount;
    }

    @Override
    public String toString() {
        return super.toString() + " [FixedDepositAccount | Matured: " + matured + "]";
    }
}
