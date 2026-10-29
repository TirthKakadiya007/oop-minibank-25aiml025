package minibank.model;

import minibank.exception.BankException;
import minibank.exception.InsufficientFundsException;
import minibank.exception.InvalidAmountException;
import minibank.model.annotation.Id;
import minibank.model.annotation.Positive;

import java.io.Serializable;
import java.util.Objects;

/**
 * Abstract base class representing a bank account (Practicals 2, 3, 5, 6, 7, 8, 9, 11, 12).
 * Implements Transactable, InterestBearing, Comparable, and Serializable.
 */
public abstract class Account implements Transactable, InterestBearing, Comparable<Account>, Serializable {
    private static final long serialVersionUID = 1L;

    private static long accountCounter = 0;

    @Id
    private final String accountNumber;

    private String ownerName;

    @Positive(message = "Account balance must be positive")
    private long balance; // Whole rupees (encapsulated, no public setter)

    private boolean active;

    /**
     * Generates standard account numbers like "AC0001", "AC0002"
     */
    private static synchronized String generateAccountNumber() {
        return String.format("AC%04d", ++accountCounter);
    }

    /**
     * Resets or aligns account counter if loading persisted accounts
     */
    public static synchronized void updateAccountCounter(long maxId) {
        if (maxId > accountCounter) {
            accountCounter = maxId;
        }
    }

    /**
     * Constructor with ownerName and opening balance (Practical 2).
     */
    public Account(String ownerName, long balance) {
        this.accountNumber = generateAccountNumber();
        this.ownerName = ownerName;
        this.balance = balance;
        this.active = true;
    }

    /**
     * Constructor chaining with this(ownerName, 0) (Practical 2).
     */
    public Account(String ownerName) {
        this(ownerName, 0L);
    }

    // Abstract methods required by Practical 5
    @Override
    public abstract double interestRate();

    public abstract boolean canWithdraw(long amount);

    // Getters - NO public setter for balance (Encapsulation requirement)
    public String getAccountNumber() {
        return accountNumber;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    @Override
    public synchronized long getBalance() {
        return balance;
    }

    public synchronized boolean isActive() {
        return active;
    }

    public synchronized void setActive(boolean active) {
        this.active = active;
    }

    /**
     * Synchronized deposit method (Practicals 2, 8, 9).
     * Thread-safe; rejects negative or zero amounts.
     */
    @Override
    public synchronized void deposit(long amount) throws InvalidAmountException {
        if (amount <= 0) {
            throw new InvalidAmountException("Deposit amount must be strictly positive: " + amount);
        }
        this.balance += amount;
    }

    /**
     * Synchronized withdraw method (Practicals 2, 8, 9).
     * Checks polymorphism via canWithdraw(amount), calculates shortfall if insufficient.
     */
    @Override
    public synchronized boolean withdraw(long amount) throws InsufficientFundsException, InvalidAmountException {
        if (amount <= 0) {
            throw new InvalidAmountException("Withdrawal amount must be strictly positive: " + amount);
        }
        if (!canWithdraw(amount)) {
            long shortfall = amount - this.balance;
            if (shortfall < 0) shortfall = 0;
            throw new InsufficientFundsException(
                    "Withdrawal failed for " + accountNumber + " (" + ownerName + "): shortfall of " + shortfall + " rupees.",
                    shortfall
            );
        }
        this.balance -= amount;
        return true;
    }

    /**
     * Transfer funds to target account using try-catch-finally and deadlock-free lock ordering (Practicals 8 & 10).
     */
    public void transfer(Account target, long amount) throws BankException {
        if (target == null) {
            throw new IllegalArgumentException("Target account cannot be null");
        }
        if (this.equals(target)) {
            throw new InvalidAmountException("Cannot transfer to the same account");
        }
        if (amount <= 0) {
            throw new InvalidAmountException("Transfer amount must be strictly positive: " + amount);
        }

        // Deadlock prevention (Practical 10): always acquire lock on smaller account number first
        Account firstLock = this.accountNumber.compareTo(target.accountNumber) < 0 ? this : target;
        Account secondLock = (firstLock == this) ? target : this;

        synchronized (firstLock) {
            synchronized (secondLock) {
                try {
                    this.withdraw(amount);
                    try {
                        target.deposit(amount);
                    } catch (InvalidAmountException e) {
                        // Rollback on target deposit failure
                        this.deposit(amount);
                        throw e;
                    }
                } catch (InsufficientFundsException | InvalidAmountException e) {
                    throw e;
                } catch (Exception e) {
                    throw new BankException("Transfer transaction encountered an unexpected error: " + e.getMessage(), e);
                } finally {
                    // Finally block executes reliably (Practical 8)
                }
            }
        }
    }

    /**
     * Override toString() (Practical 3, 5).
     */
    @Override
    public String toString() {
        return accountNumber + " | " + ownerName + " | " + balance + " INR | " +
                (active ? "ACTIVE" : "INACTIVE") + " | Rate: " + interestRate() + "%";
    }

    /**
     * Override equals(Object) based on accountNumber (Practical 3).
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Account other)) return false;
        return Objects.equals(accountNumber, other.accountNumber);
    }

    /**
     * Override hashCode() agreeing with equals() (Practical 3).
     */
    @Override
    public int hashCode() {
        return Objects.hash(accountNumber);
    }

    /**
     * Comparable implementation comparing account numbers (Practical 12).
     */
    @Override
    public int compareTo(Account other) {
        return this.accountNumber.compareTo(other.accountNumber);
    }
}
