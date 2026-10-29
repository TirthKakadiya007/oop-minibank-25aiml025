package minibank.service;

import minibank.exception.BankException;
import minibank.model.Account;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Transaction processing engine managing thread pools, concurrent storage,
 * wait/notify producer-consumer queues, and deadlock-safe execution (Practicals 10 & 12).
 */
public class TransactionProcessor {

    private final ExecutorService executor;
    private final ConcurrentHashMap<String, Account> sharedAccounts = new ConcurrentHashMap<>();
    private final AtomicInteger completedTasks = new AtomicInteger(0);

    public TransactionProcessor() {
        // Practical 10: Fixed thread pool of 4 worker threads
        this.executor = Executors.newFixedThreadPool(4);
    }

    /**
     * Submits a Runnable task to the managed thread pool.
     */
    public void submit(Runnable task) {
        executor.execute(() -> {
            try {
                task.run();
                completedTasks.incrementAndGet();
            } catch (Exception e) {
                System.err.println("Error executing async transaction: " + e.getMessage());
            }
        });
    }

    /**
     * Gracefully stops the thread pool and waits for active tasks to terminate.
     */
    public void stop() {
        executor.shutdown();
        try {
            if (!executor.awaitTermination(10, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    public int getCompletedTaskCount() {
        return completedTasks.get();
    }

    public ConcurrentHashMap<String, Account> getSharedAccounts() {
        return sharedAccounts;
    }

    public void addSharedAccount(Account account) {
        if (account != null) {
            sharedAccounts.put(account.getAccountNumber(), account);
        }
    }

    // -------------------------------------------------------------
    // Producer-Consumer Demonstration with wait() / notify() (Practical 10)
    // -------------------------------------------------------------
    public static class TransactionQueue {
        private final String[] buffer;
        private int count = 0;
        private int putIndex = 0;
        private int takeIndex = 0;
        private final int capacity;

        public TransactionQueue(int capacity) {
            this.capacity = capacity;
            this.buffer = new String[capacity];
        }

        public synchronized void produce(String tx) throws InterruptedException {
            while (count == capacity) {
                wait(); // Wait until buffer has space
            }
            buffer[putIndex] = tx;
            putIndex = (putIndex + 1) % capacity;
            count++;
            notifyAll(); // Notify consumer
        }

        public synchronized String consume() throws InterruptedException {
            while (count == 0) {
                wait(); // Wait until buffer has an item
            }
            String item = buffer[takeIndex];
            takeIndex = (takeIndex + 1) % capacity;
            count--;
            notifyAll(); // Notify producer
            return item;
        }

        public synchronized int size() {
            return count;
        }
    }

    // -------------------------------------------------------------
    // Deadlock-Free Transfer Demonstration (Practical 10)
    // -------------------------------------------------------------
    /**
     * Safe transfer utilizing consistent lock ordering (locking smaller accountNumber first).
     */
    public static void safeTransfer(Account from, Account to, long amount) throws BankException {
        from.transfer(to, amount);
    }
}
