package minibank.model;

import java.io.Serializable;

/**
 * Record representing bank institution metadata (Practical 1).
 * Records automatically provide constructors, getters, equals, hashCode, and toString.
 */
public record BankInfo(String name, String branch) implements Serializable {
    private static final long serialVersionUID = 1L;
}
