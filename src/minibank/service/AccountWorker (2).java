package minibank.service;

import minibank.exception.InvalidAmountException;
import minibank.model.Account;

/**
 * Worker thread executing repeated deposits to demonstrate thread safety and race conditions (Practical 9).
 */
public class AccountWorker implements Runnable {

    private final Account account;
    private final int times;
    private final long amount;

    public AccountWorker(Account account, int times, long amount) {
        this.account = account;
        this.times = times;
        this.amount = amount;
    }

    @Override
    public void run() {
        String threadName = Thread.currentThread().getName();
        // System.out.println("Starting thread: " + threadName);
        for (int i = 0; i < times; i++) {
            try {
                account.deposit(amount);
            } catch (InvalidAmountException e) {
                System.err.println("[" + threadName + "] Deposit error: " + e.getMessage());
            }
        }
        // System.out.println("Finished thread: " + threadName);
    }
}
