package minibank.util;

import minibank.model.Account;
import minibank.model.SavingsAccount;
import minibank.model.CurrentAccount;
import minibank.model.FixedDepositAccount;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Utility to generate formatted, multi-line account statements using StringBuilder (Practical 4).
 */
public final class StatementFormatter {

    private StatementFormatter() {
        // Prevent instantiation
    }

    public static String buildStatement(Account account) {
        return buildStatement(account, List.of());
    }

    public static String buildStatement(Account account, List<String> recentTransactions) {
        if (account == null) {
            return "ERROR: Account is null";
        }

        StringBuilder sb = new StringBuilder();
        String border = "================================================================";
        String line =   "----------------------------------------------------------------";

        sb.append(border).append("\n");
        sb.append("                  MINIBANK ACCOUNT STATEMENT                    \n");
        sb.append(border).append("\n");
        sb.append(String.format(" Generated On: %s\n", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))));
        sb.append(line).append("\n");
        sb.append(String.format(" Account Number : %s\n", account.getAccountNumber()));
        sb.append(String.format(" Account Holder : %s\n", account.getOwnerName()));
        sb.append(String.format(" Status         : %s\n", account.isActive() ? "ACTIVE" : "INACTIVE"));

        // Pattern matching / subtype specifics
        if (account instanceof SavingsAccount sa) {
            sb.append(String.format(" Account Type   : Savings Account (Min Balance: %d INR)\n", sa.getMinBalance()));
        } else if (account instanceof CurrentAccount ca) {
            sb.append(String.format(" Account Type   : Current Account (Overdraft Limit: %d INR)\n", ca.getOverdraftLimit()));
        } else if (account instanceof FixedDepositAccount fda) {
            sb.append(String.format(" Account Type   : Fixed Deposit (Locked/Matured: %s)\n", fda.isMatured()));
        } else {
            sb.append(String.format(" Account Type   : %s\n", account.getClass().getSimpleName()));
        }

        sb.append(String.format(" Current Balance: %d INR\n", account.getBalance()));
        sb.append(String.format(" Interest Rate  : %.2f%% p.a.\n", account.interestRate()));
        sb.append(String.format(" Est. Yearly Int: %.2f INR\n", account.yearlyInterest()));
        sb.append(String.format(" Est. Monthly In: %.2f INR\n", account.monthlyInterest()));
        sb.append(line).append("\n");

        if (recentTransactions != null && !recentTransactions.isEmpty()) {
            sb.append(" RECENT ACTIVITY:\n");
            for (String tx : recentTransactions) {
                sb.append("   * ").append(tx).append("\n");
            }
            sb.append(line).append("\n");
        }

        sb.append(" Thank you for banking with MiniBank!\n");
        sb.append(border).append("\n");

        return sb.toString();
    }
}
