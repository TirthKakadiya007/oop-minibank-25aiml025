package minibank.model;

/**
 * Enum defining the nine navigation options available in MiniBank.
 */
public enum MenuOption {
    OPEN_ACCOUNT(1, "Open New Account"),
    DEPOSIT(2, "Deposit Funds"),
    WITHDRAW(3, "Withdraw Funds"),
    TRANSFER(4, "Transfer Funds"),
    STATEMENT(5, "Print Account Statement"),
    LIST_ACCOUNTS(6, "List All Accounts (Sorted by Balance)"),
    SEARCH_ACCOUNTS(7, "Search Account by Owner Name"),
    BATCH_PROCESS(8, "Run Safe Batch Transactions (ThreadPool)"),
    EXIT(9, "Save & Exit");

    private final int optionNumber;
    private final String description;

    MenuOption(int optionNumber, String description) {
        this.optionNumber = optionNumber;
        this.description = description;
    }

    public int getOptionNumber() {
        return optionNumber;
    }

    public String getDescription() {
        return description;
    }

    public static MenuOption fromNumber(int number) {
        for (MenuOption opt : values()) {
            if (opt.optionNumber == number) {
                return opt;
            }
        }
        return null;
    }
}
