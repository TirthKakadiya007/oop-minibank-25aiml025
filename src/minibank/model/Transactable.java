package minibank.model;

import minibank.exception.BankException;

/**
 * Interface defining transactional capabilities for accounts.
 */
public interface Transactable {
    void deposit(long amount) throws BankException;
    boolean withdraw(long amount) throws BankException;
}
