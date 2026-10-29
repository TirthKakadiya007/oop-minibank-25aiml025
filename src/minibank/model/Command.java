package minibank.model;

import java.io.Serializable;

/**
 * Record representing a parsed banking command (Practical 4).
 */
public record Command(TransactionType type, String accountNumber, long amount) implements Serializable {
    private static final long serialVersionUID = 1L;
}
