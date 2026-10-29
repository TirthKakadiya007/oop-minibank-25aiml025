package minibank.repository;

import minibank.model.Account;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * AccountStore demonstrating Java Collections Framework (Practical 12).
 * Uses HashMap/TreeMap, ArrayList for transaction history, and Comparator for sorting.
 */
public class AccountStore {

    private final Map<String, Account> accounts = new HashMap<>();
    private final List<String> transactionHistory = new ArrayList<>();

    public void add(Account account) {
        if (account != null) {
            accounts.put(account.getAccountNumber(), account);
        }
    }

    public Account get(String accountNumber) {
        if (accountNumber == null) return null;
        return accounts.get(accountNumber);
    }

    public ArrayList<Account> getAll() {
        return new ArrayList<>(accounts.values());
    }

    public void logTransaction(String entry) {
        if (entry != null) {
            transactionHistory.add(entry);
        }
    }

    public List<String> getTransactionHistory() {
        return Collections.unmodifiableList(transactionHistory);
    }

    /**
     * Returns an ArrayList<Account> sorted by balance in descending order using a Comparator (Practical 12).
     */
    public ArrayList<Account> sortedByBalance() {
        ArrayList<Account> list = new ArrayList<>(accounts.values());
        list.sort(Comparator.comparingLong(Account::getBalance).reversed());
        return list;
    }

    /**
     * Supplementary: Returns all accounts with balance greater than or equal to threshold.
     */
    public ArrayList<Account> getAccountsWithBalanceAbove(long threshold) {
        ArrayList<Account> result = new ArrayList<>();
        for (Account acc : accounts.values()) {
            if (acc.getBalance() >= threshold) {
                result.add(acc);
            }
        }
        return result;
    }

    /**
     * Supplementary: Returns a TreeMap where accounts are automatically sorted by account number key.
     */
    public TreeMap<String, Account> toTreeMap() {
        return new TreeMap<>(accounts);
    }

    public int size() {
        return accounts.size();
    }
}
