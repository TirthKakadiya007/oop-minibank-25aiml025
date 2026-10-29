package minibank.util;

import minibank.model.Account;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;

/**
 * Persistence utility using Object Serialization (Practical 11).
 * Saves and reloads accounts using ObjectOutputStream and ObjectInputStream inside try-with-resources.
 */
public final class StatePersister {

    private StatePersister() {
        // Prevent instantiation
    }

    /**
     * Serializes an array of accounts to the specified file path.
     */
    public static void save(Account[] accounts, Path file) throws IOException {
        if (file.getParent() != null) {
            Files.createDirectories(file.getParent());
        }
        try (ObjectOutputStream oos = new ObjectOutputStream(Files.newOutputStream(file))) {
            oos.writeObject(accounts);
        }
    }

    /**
     * Serializes a collection of accounts to the specified file path.
     */
    public static void save(Collection<Account> accounts, Path file) throws IOException {
        save(accounts.toArray(new Account[0]), file);
    }

    /**
     * Deserializes an array of accounts from the specified file path.
     */
    public static Account[] load(Path file) throws IOException, ClassNotFoundException {
        if (!Files.exists(file)) {
            return new Account[0];
        }
        try (ObjectInputStream ois = new ObjectInputStream(Files.newInputStream(file))) {
            Account[] accounts = (Account[]) ois.readObject();
            // Re-align account counter to highest loaded ID
            for (Account acc : accounts) {
                if (acc != null && acc.getAccountNumber().startsWith("AC")) {
                    try {
                        long num = Long.parseLong(acc.getAccountNumber().substring(2));
                        Account.updateAccountCounter(num);
                    } catch (NumberFormatException ignored) {
                    }
                }
            }
            return accounts;
        }
    }
}
