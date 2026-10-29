package minibank.model;

/**
 * Enum defining the navigation and action options available in MiniBank (Practicals 1 & 14).
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
    SUMMARY_REPORT(9, "Generate End-of-Day Audit Report (NIO)"),
    WORKING_HOURS(10, "View Bank Working Hours"),
    RUN_LAB_DEMOS(11, "Run Complete All-Practical Verification Demo (Lab 1-14)"),
    EXIT(12, "Save & Exit");

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
