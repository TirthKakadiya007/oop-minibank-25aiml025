package minibank.util;

import minibank.model.Command;
import minibank.model.TransactionType;

/**
 * Command parsing utility (Practical 4).
 * Parses typed lines like "DEPOSIT AC0001 500" into typed Command records.
 */
public final class CommandParser {

    private CommandParser() {
        // Prevent instantiation
    }

    /**
     * Splits and parses an input line into a Command object.
     * @param line string in format "TYPE ACCOUNT_NUMBER AMOUNT"
     * @return Command record
     * @throws IllegalArgumentException if malformed or parts count is wrong
     */
    public static Command parse(String line) {
        if (line == null || line.isBlank()) {
            throw new IllegalArgumentException("Command line cannot be empty");
        }

        String[] parts = line.trim().split("\\s+");
        if (parts.length != 3) {
            throw new IllegalArgumentException(
                    "Invalid command format. Expected 3 tokens: <TYPE> <ACCOUNT_NUM> <AMOUNT>, but found " + parts.length + " tokens in: \"" + line + "\""
            );
        }

        TransactionType type;
        try {
            type = TransactionType.valueOf(parts[0].toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Unknown transaction type: " + parts[0] + ". Valid types: DEPOSIT, WITHDRAW, TRANSFER");
        }

        String accountNumber = parts[1].toUpperCase();

        long amount;
        try {
            amount = Long.parseLong(parts[2]);
            if (amount <= 0) {
                throw new IllegalArgumentException("Amount must be greater than zero: " + amount);
            }
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid amount format: " + parts[2]);
        }

        return new Command(type, accountNumber, amount);
    }
}
