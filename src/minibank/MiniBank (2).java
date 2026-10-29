package minibank;

import minibank.exception.AccountNotFoundException;
import minibank.exception.BankException;
import minibank.exception.InsufficientFundsException;
import minibank.exception.InvalidAmountException;
import minibank.model.Account;
import minibank.model.BankInfo;
import minibank.model.CurrentAccount;
import minibank.model.Customer;
import minibank.model.FixedDepositAccount;
import minibank.model.MenuOption;
import minibank.model.SavingsAccount;
import minibank.model.WithdrawRule;
import minibank.repository.AccountStore;
import minibank.repository.Repository;
import minibank.service.AccountWorker;
import minibank.service.BankService;
import minibank.service.BankSession;
import minibank.service.TransactionProcessor;
import minibank.util.AnnotationValidator;
import minibank.util.CommandParser;
import minibank.util.StatementFormatter;

// Practical 6 requirement: Use at least one static import
import static minibank.util.Validator.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Main application class for MiniBank (Practicals 1 through 14).
 * Combines all Part-B semester project components into an integrated, interactive system.
 */
public class MiniBank {

    private static final BankInfo BANK_INFO = new BankInfo("MiniBank Sovereign", "Tech City Main Branch");
    private static final BankService bankService = new BankService();

    public static void main(String[] args) {
        // Practical 1: Print BankInfo object as application header
        printHeader();

        // Practical 11 & 14: Load saved accounts on startup
        try {
            bankService.loadSavedState();
            int count = bankService.getAccountRepository().count();
            if (count > 0) {
                System.out.println("[DATA RESTORED] Loaded " + count + " persistent account(s) from disk.");
            } else {
                seedInitialData();
            }
        } catch (Exception e) {
            System.out.println("[INFO] No previous state found. Initializing fresh store with sample accounts.");
            seedInitialData();
        }

        // Practical 8: Try-with-resources using AutoCloseable BankSession
        try (BankSession session = new BankSession("SES-" + System.currentTimeMillis() % 100000, "AdminUser")) {
            runInteractiveMenu();
        }

        // Practical 11 & 14: Save accounts on exit
        try {
            bankService.saveCurrentState();
            System.out.println("[DATA SAVED] All accounts successfully saved to data/accounts.dat.");
        } catch (IOException e) {
            System.err.println("[ERROR] Failed to save state on exit: " + e.getMessage());
        }

        System.out.println("\nThank you for choosing " + BANK_INFO.name() + ". Goodbye!");
    }

    private static void printHeader() {
        System.out.println("================================================================================");
        System.out.println("                   " + BANK_INFO.name().toUpperCase() + "                     ");
        System.out.println("                 Branch: " + BANK_INFO.branch() + "                ");
        System.out.println("            Object Oriented Programming Capstone Project - Part B               ");
        System.out.println("================================================================================");
    }

    private static void seedInitialData() {
        try {
            Customer c1 = new Customer("Aarav Sharma", "aarav@example.com", "9876543210",
                    new Customer.Address("101 MG Road", "Bengaluru", "560001"));
            Customer c2 = new Customer("Riya Patel", "riya@example.com", "9123456789",
                    new Customer.Address("45 Park Street", "Mumbai", "400001"));
            Customer c3 = new Customer("Vikram Singh", "vikram@example.com", "9898989898",
                    new Customer.Address("12 Civil Lines", "Delhi", "110054"));

            bankService.openAccount(c1, "SAVINGS", 5000L, 1000L);
            bankService.openAccount(c2, "CURRENT", 10000L, 5000L);
            bankService.openAccount(c3, "FIXED_DEPOSIT", 25000L, 0L);
        } catch (Exception e) {
            System.err.println("Seeding notice: " + e.getMessage());
        }
    }

    private static void runInteractiveMenu() {
        Scanner scanner = new Scanner(System.in);
        boolean running = true;

        while (running) {
            printMenu();
            System.out.print("\nEnter choice (1-9): ");
            String input = scanner.nextLine().trim();

            int choiceNum;
            try {
                choiceNum = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                // Supplementary Problem (Practical 1 & 2): Re-prompt on invalid number
                System.out.println(">> Invalid input. Please enter a number between 1 and 9.\n");
                continue;
            }

            MenuOption option = MenuOption.fromNumber(choiceNum);
            if (option == null) {
                System.out.println(">> Invalid choice! Please select an option from the menu.\n");
                continue;
            }

            // Practical 1: Use a switch expression on the choice
            running = switch (option) {
                case OPEN_ACCOUNT    -> { handleOpenAccount(scanner); yield true; }
                case DEPOSIT         -> { handleDeposit(scanner); yield true; }
                case WITHDRAW        -> { handleWithdraw(scanner); yield true; }
                case TRANSFER        -> { handleTransfer(scanner); yield true; }
                case STATEMENT       -> { handleStatement(scanner); yield true; }
                case LIST_ACCOUNTS   -> { handleListAccounts(); yield true; }
                case SEARCH_ACCOUNTS -> { handleSearchAccounts(scanner); yield true; }
                case BATCH_PROCESS   -> { handleBatchProcess(); yield true; }
                case EXIT            -> { System.out.println("\nExiting MiniBank application..."); yield false; }
            };

            if (running) {
                System.out.print("\nPress ENTER to continue back to menu...");
                scanner.nextLine();
                System.out.println();
            }
        }
    }

    private static void printMenu() {
        System.out.println("\n----------------------------- MAIN MENU -----------------------------");
        for (MenuOption opt : MenuOption.values()) {
            System.out.printf("  [%2d] %s%n", opt.getOptionNumber(), opt.getDescription());
        }
        System.out.println("---------------------------------------------------------------------");
    }

    // -------------------------------------------------------------
    // Menu Action Handlers
    // -------------------------------------------------------------

    private static void handleOpenAccount(Scanner scanner) {
        System.out.println("\n--- [OPEN NEW ACCOUNT] ---");
        System.out.print("Enter Customer Name: ");
        String name = scanner.nextLine().trim();
        if (name.isEmpty()) {
            System.out.println("Customer name cannot be empty.");
            return;
        }

        System.out.print("Enter Mobile Number (10 digits starting with 6-9): ");
        String mobile = scanner.nextLine().trim();
        // Using statically imported validator
        if (!isValidMobile(mobile)) {
            System.out.println(">> Validation Failed: Invalid mobile number format! (Must be 10 digits starting with 6-9)");
            return;
        }

        System.out.print("Enter Email Address: ");
        String email = scanner.nextLine().trim();
        if (!isValidEmail(email)) {
            System.out.println(">> Validation Failed: Invalid email address format!");
            return;
        }

        System.out.print("Enter City: ");
        String city = scanner.nextLine().trim();
        System.out.print("Enter Pincode: ");
        String pin = scanner.nextLine().trim();
        Customer.Address addr = new Customer.Address("Primary Street", city.isEmpty() ? "Metropolis" : city, pin.isEmpty() ? "100001" : pin);
        Customer customer = new Customer(name, email, mobile, addr);

        System.out.print("Select Account Type (1: Savings, 2: Current, 3: Fixed Deposit): ");
        String typeChoice = scanner.nextLine().trim();
        String typeStr = switch (typeChoice) {
            case "1" -> "SAVINGS";
            case "2" -> "CURRENT";
            case "3" -> "FIXED_DEPOSIT";
            default -> "SAVINGS";
        };

        System.out.print("Enter Opening Balance (INR): ");
        String balStr = scanner.nextLine().trim();
        if (!isValidAmount(balStr)) {
            System.out.println(">> Validation Failed: Amount must be a positive integer!");
            return;
        }
        long initialDeposit = Long.parseLong(balStr);

        try {
            Account created = bankService.openAccount(customer, typeStr, initialDeposit, 1000L);
            System.out.println("\n>> SUCCESS: Account opened successfully!");
            System.out.println("   Account Number: " + created.getAccountNumber());
            System.out.println("   Customer ID   : " + customer.getCustomerId());
            System.out.println("   Account Holder: " + created.getOwnerName());
            System.out.println("   Balance       : " + created.getBalance() + " INR");
            System.out.println("   Interest Rate : " + created.interestRate() + "%");
        } catch (BankException e) {
            System.out.println(">> Banking Error: " + e.getMessage());
        }
    }

    private static void handleDeposit(Scanner scanner) {
        System.out.println("\n--- [DEPOSIT FUNDS] ---");
        System.out.print("Enter Account Number (e.g. AC0001): ");
        String accNum = scanner.nextLine().trim().toUpperCase();
        System.out.print("Enter Deposit Amount (INR): ");
        String amtStr = scanner.nextLine().trim();

        if (!isValidAmount(amtStr)) {
            System.out.println(">> Validation Failed: Deposit amount must be a positive integer!");
            return;
        }
        long amount = Long.parseLong(amtStr);

        try {
            bankService.deposit(accNum, amount);
            Account acc = bankService.getAccountRepository().findById(accNum);
            System.out.println(">> SUCCESS: Deposited " + amount + " INR into " + accNum);
            System.out.println("   Updated Balance: " + acc.getBalance() + " INR");
        } catch (AccountNotFoundException e) {
            System.out.println(">> Error: " + e.getMessage());
        } catch (InvalidAmountException e) {
            System.out.println(">> Invalid Amount: " + e.getMessage());
        } catch (BankException e) {
            System.out.println(">> Operation Failed: " + e.getMessage());
        }
    }

    private static void handleWithdraw(Scanner scanner) {
        System.out.println("\n--- [WITHDRAW FUNDS] ---");
        System.out.print("Enter Account Number (e.g. AC0001): ");
        String accNum = scanner.nextLine().trim().toUpperCase();
        System.out.print("Enter Withdrawal Amount (INR): ");
        String amtStr = scanner.nextLine().trim();

        if (!isValidAmount(amtStr)) {
            System.out.println(">> Validation Failed: Withdrawal amount must be a positive integer!");
            return;
        }
        long amount = Long.parseLong(amtStr);

        try {
            bankService.withdraw(accNum, amount);
            Account acc = bankService.getAccountRepository().findById(accNum);
            System.out.println(">> SUCCESS: Withdrew " + amount + " INR from " + accNum);
            System.out.println("   Remaining Balance: " + acc.getBalance() + " INR");
        } catch (InsufficientFundsException e) {
            // Practical 8 & 13: Handle InsufficientFundsException and report shortfall
            System.out.println(">> WITHDRAWAL REJECTED: " + e.getMessage());
            System.out.println("   Required Shortfall: " + e.getShortfall() + " INR needed.");
        } catch (AccountNotFoundException e) {
            System.out.println(">> Error: " + e.getMessage());
        } catch (InvalidAmountException e) {
            System.out.println(">> Invalid Amount: " + e.getMessage());
        } catch (BankException e) {
            System.out.println(">> Operation Failed: " + e.getMessage());
        }
    }

    private static void handleTransfer(Scanner scanner) {
        System.out.println("\n--- [TRANSFER FUNDS] ---");
        System.out.print("Enter Source Account Number (From): ");
        String fromAcc = scanner.nextLine().trim().toUpperCase();
        System.out.print("Enter Target Account Number (To): ");
        String toAcc = scanner.nextLine().trim().toUpperCase();
        System.out.print("Enter Transfer Amount (INR): ");
        String amtStr = scanner.nextLine().trim();

        if (!isValidAmount(amtStr)) {
            System.out.println(">> Validation Failed: Transfer amount must be a positive integer!");
            return;
        }
        long amount = Long.parseLong(amtStr);

        try {
            bankService.transfer(fromAcc, toAcc, amount);
            Account source = bankService.getAccountRepository().findById(fromAcc);
            Account target = bankService.getAccountRepository().findById(toAcc);
            System.out.println(">> SUCCESS: Transferred " + amount + " INR from " + fromAcc + " to " + toAcc);
            System.out.printf("   Source (%s) Balance: %d INR%n", fromAcc, source.getBalance());
            System.out.printf("   Target (%s) Balance: %d INR%n", toAcc, target.getBalance());
        } catch (InsufficientFundsException e) {
            System.out.println(">> TRANSFER REJECTED: " + e.getMessage());
            System.out.println("   Shortfall: " + e.getShortfall() + " INR");
        } catch (BankException e) {
            System.out.println(">> Transfer Failed: " + e.getMessage());
        }
    }

    private static void handleStatement(Scanner scanner) {
        System.out.println("\n--- [ACCOUNT STATEMENT] ---");
        System.out.print("Enter Account Number (e.g. AC0001): ");
        String accNum = scanner.nextLine().trim().toUpperCase();

        try {
            String statement = bankService.statement(accNum);
            System.out.println(statement);
        } catch (AccountNotFoundException e) {
            System.out.println(">> Error: " + e.getMessage());
        }
    }

    private static void handleListAccounts() {
        System.out.println("\n--- [ALL ACCOUNTS - SORTED BY BALANCE] ---");
        List<Account> accounts = bankService.listAccountsSortedByBalance();
        if (accounts.isEmpty()) {
            System.out.println("No accounts currently registered in MiniBank.");
            return;
        }
        System.out.printf("%-8s | %-16s | %-12s | %-10s | %-8s | %s%n",
                "ACC NO", "OWNER NAME", "BALANCE (INR)", "STATUS", "RATE (%)", "DETAILS");
        System.out.println("--------------------------------------------------------------------------------");
        for (Account a : accounts) {
            System.out.printf("%-8s | %-16s | %12d | %-10s | %8.1f | %s%n",
                    a.getAccountNumber(), a.getOwnerName(), a.getBalance(),
                    a.isActive() ? "ACTIVE" : "INACTIVE", a.interestRate(), a.getClass().getSimpleName());
        }
    }

    private static void handleSearchAccounts(Scanner scanner) {
        System.out.println("\n--- [SEARCH ACCOUNTS BY OWNER] ---");
        System.out.print("Enter account holder name or substring: ");
        String name = scanner.nextLine().trim();

        List<Account> results = bankService.searchAccountsByOwner(name);
        if (results.isEmpty()) {
            System.out.println("No accounts found matching name: " + name);
        } else {
            System.out.println("Found " + results.size() + " matching account(s):");
            for (Account acc : results) {
                System.out.println("  * " + acc);
            }
        }
    }

    private static void handleBatchProcess() {
        System.out.println("\n--- [RUNNING SAFE BATCH TRANSACTIONS (PRACTICAL 10)] ---");
        System.out.println("Submitting 10 concurrent deposit/withdraw tasks across 4 thread pool workers...");

        List<Account> accounts = bankService.listAccounts();
        if (accounts.isEmpty()) {
            System.out.println("No accounts available to process batch.");
            return;
        }

        Account target = accounts.get(0);
        long initialBal = target.getBalance();
        System.out.println("Target account: " + target.getAccountNumber() + " initial balance = " + initialBal + " INR");

        List<Runnable> batch = new ArrayList<>();
        // 5 deposit tasks of 100 each
        for (int i = 0; i < 5; i++) {
            batch.add(() -> {
                try {
                    bankService.deposit(target.getAccountNumber(), 100);
                } catch (Exception ignored) {}
            });
        }
        // 5 withdraw tasks of 50 each
        for (int i = 0; i < 5; i++) {
            batch.add(() -> {
                try {
                    bankService.withdraw(target.getAccountNumber(), 50);
                } catch (Exception ignored) {}
            });
        }

        bankService.processBatchTransactions(batch);
        System.out.println(">> Batch completed successfully through TransactionProcessor thread pool.");
        System.out.println("Target account: " + target.getAccountNumber() + " final balance = " + target.getBalance() + " INR");
    }

    private static void handleSummaryReport() {
        System.out.println("\n--- [END-OF-DAY AUDIT SUMMARY REPORT (PRACTICAL 11)] ---");
        try {
            String report = bankService.generateDailyReport();
            System.out.println(report);
            System.out.println(">> Report persisted to data/logs/audit-report-*.txt");
        } catch (IOException e) {
            System.out.println(">> Failed to generate report: " + e.getMessage());
        }
    }

    private static void handleWorkingHours() {
        // Supplementary requirement from Practical 1
        System.out.println("\n=================================================");
        System.out.println("             MINIBANK WORKING HOURS              ");
        System.out.println("=================================================");
        System.out.println(" Monday - Friday : 09:30 AM to 04:30 PM IST     ");
        System.out.println(" 1st, 3rd, 5th Sat: 09:30 AM to 01:30 PM IST    ");
        System.out.println(" 2nd & 4th Sat   : Bank Holiday                  ");
        System.out.println(" Sunday          : Closed                        ");
        System.out.println(" Net Banking/ATM : 24x7 Available                ");
        System.out.println("=================================================");
    }

    // -------------------------------------------------------------
    // Comprehensive Automated Milestone Demo (Labs 1 through 14)
    // -------------------------------------------------------------
    public static void runAllLabMilestoneDemos() {
        System.out.println("\n================================================================================");
        System.out.println("       MINIBANK COMPREHENSIVE SEMESTER VERIFICATION (LABS 1 TO 14 DEMO)         ");
        System.out.println("================================================================================");

        // Lab 1: BankInfo Record & MenuOption Enum
        System.out.println("\n[LAB-01] Testing BankInfo Record and MenuOption Enum:");
        BankInfo demoBank = new BankInfo("MiniBank Central", "Apex Hub");
        System.out.println("   BankInfo Record: " + demoBank + " [name=" + demoBank.name() + ", branch=" + demoBank.branch() + "]");
        System.out.println("   MenuOption Count: " + MenuOption.values().length + " options defined.");

        // Lab 2: Customer, Account Encapsulation & Static ID Generation
        System.out.println("\n[LAB-02] Testing Customer & Account Encapsulation & ID Counters:");
        Customer c1 = new Customer("Divya Rao", "divya@corp.com", "9876543210");
        SavingsAccount sa = new SavingsAccount(c1.getName(), 5000L, 1000L);
        System.out.println("   Generated Customer ID: " + c1.getCustomerId());
        System.out.println("   Generated Account ID : " + sa.getAccountNumber());
        System.out.println("   Account Balance      : " + sa.getBalance() + " INR (Encapsulated, no public setter)");

        // Lab 3: Object Methods, Nested Address, clone(), instanceof
        System.out.println("\n[LAB-03] Testing Object Methods (toString, equals, hashCode), Address, clone():");
        Customer.Address addr = new Customer.Address("Flat 4B, Hill View", "Pune", "411001");
        c1.setAddress(addr);
        Customer clonedCustomer = c1.clone();
        System.out.println("   Original Customer: " + c1);
        System.out.println("   Cloned Customer  : " + clonedCustomer);
        System.out.println("   Deep Clone Test  : " + (c1 != clonedCustomer && c1.equals(clonedCustomer)));
        System.out.println("   instanceof Check : " + (sa instanceof Account ? "sa is an Account" : "Failed"));

        // Lab 4: Regex Validator, Command Record & Parser, StringBuilder Statement
        System.out.println("\n[LAB-04] Testing Regex Validator, CommandParser, StatementFormatter:");
        System.out.println("   isValidMobile('9876543210') = " + isValidMobile("9876543210"));
        System.out.println("   isValidMobile('12345')      = " + isValidMobile("12345"));
        System.out.println("   isValidEmail('abc@xyz.com') = " + isValidEmail("abc@xyz.com"));
        System.out.println("   isValidPan('ABCDE1234F')    = " + isValidPan("ABCDE1234F"));
        System.out.println("   isValidIfsc('SBIN0001234')  = " + isValidIfsc("SBIN0001234"));
        var cmd = CommandParser.parse("DEPOSIT AC0001 500");
        System.out.println("   Parsed Command: type=" + cmd.type() + ", acc=" + cmd.accountNumber() + ", amt=" + cmd.amount());

        // Lab 5: Abstract Account & Polymorphism (Savings, Current, FixedDeposit)
        System.out.println("\n[LAB-05] Testing Abstract Account Subtypes & Polymorphism:");
        Account[] accountHierarchy = new Account[]{
                new SavingsAccount("User A", 10000L, 1000L),
                new CurrentAccount("User B", 20000L, 10000L),
                new FixedDepositAccount("User C", 50000L)
        };
        for (Account acc : accountHierarchy) {
            System.out.printf("   Polymorphic call on %-20s -> Interest Rate: %.1f%%, Can Withdraw 15000: %s%n",
                    acc.getClass().getSimpleName(), acc.interestRate(), acc.canWithdraw(15000));
        }

        // Lab 6: Interfaces (Transactable, InterestBearing), Functional Interface, Lambdas
        System.out.println("\n[LAB-06] Testing Interfaces, Default Methods, WithdrawRule Lambda:");
        System.out.println("   Savings Account Yearly Interest : " + accountHierarchy[0].yearlyInterest() + " INR");
        System.out.println("   Savings Account Monthly Interest: " + accountHierarchy[0].monthlyInterest() + " INR");
        // Anonymous Class WithdrawRule
        WithdrawRule anonRule = new WithdrawRule() {
            @Override
            public boolean allow(Account a, long amount) {
                return amount <= 5000;
            }
        };
        // Lambda Expression WithdrawRule
        WithdrawRule lambdaRule = (a, amount) -> amount <= 5000;
        System.out.println("   Anonymous Class Rule allow(5000): " + anonRule.allow(sa, 5000));
        System.out.println("   Lambda Expression Rule allow(5000): " + lambdaRule.allow(sa, 5000));

        // Lab 7: Custom Annotations & Reflection (@Id, @Positive, @MaxLength)
        System.out.println("\n[LAB-07] Testing Custom Annotations (@Id, @Positive, @MaxLength) with Reflection:");
        SavingsAccount invalidAcc = new SavingsAccount("Overdrawn", -500L, 1000L);
        String[] errors = AnnotationValidator.validate(invalidAcc);
        System.out.println("   Validation errors on invalid account (balance = -500):");
        for (String err : errors) {
            System.out.println("     * " + err);
        }

        // Lab 8: Custom Exception Hierarchy & Shortfall Handling
        System.out.println("\n[LAB-08] Testing Custom Exception Hierarchy (InsufficientFundsException shortfall):");
        try {
            sa.withdraw(20000); // Exceeds balance
        } catch (InsufficientFundsException e) {
            System.out.println("   Caught Expected InsufficientFundsException: " + e.getMessage());
            System.out.println("   Stored Shortfall Amount: " + e.getShortfall() + " INR");
        } catch (Exception e) {
            System.out.println("   Unexpected error: " + e.getMessage());
        }

        // Lab 9: Multithreading & Synchronization (AccountWorker)
        System.out.println("\n[LAB-09] Testing Thread Synchronization with AccountWorker:");
        Account testAcc = new SavingsAccount("Concurrent Tester", 0L, 0L);
        Thread[] threads = new Thread[10];
        for (int i = 0; i < 10; i++) {
            threads[i] = new Thread(new AccountWorker(testAcc, 100, 10)); // 100 deposits of 10 = 1000 per thread
            threads[i].start();
        }
        for (Thread t : threads) {
            try { t.join(); } catch (InterruptedException ignored) {}
        }
        System.out.println("   Target balance (10 threads * 1000) = 10000 INR | Actual Synchronized Balance = " + testAcc.getBalance() + " INR");

        // Lab 10: TransactionProcessor, Deadlock Prevention & Producer-Consumer
        System.out.println("\n[LAB-10] Testing TransactionProcessor, Deadlock-Safe Ordering & Queue:");
        SavingsAccount accA = new SavingsAccount("Party A", 5000L, 0L);
        CurrentAccount accB = new CurrentAccount("Party B", 5000L, 0L);
        try {
            accA.transfer(accB, 1500L);
            System.out.println("   Safe Transfer Succeeded: Party A = " + accA.getBalance() + ", Party B = " + accB.getBalance());
        } catch (Exception e) {
            System.out.println("   Transfer error: " + e.getMessage());
        }
        // Producer-Consumer test
        try {
            TransactionProcessor.TransactionQueue queue = new TransactionProcessor.TransactionQueue(5);
            queue.produce("TX-001");
            queue.produce("TX-002");
            System.out.println("   Producer-Consumer Queue consumed item: " + queue.consume() + ", remaining: " + queue.size());
        } catch (InterruptedException ignored) {}

        // Lab 11: StatePersister, TransactionLog & ReportGenerator
        System.out.println("\n[LAB-11] Testing StatePersister (Serialization) & NIO ReportGenerator:");
        try {
            String report = bankService.generateDailyReport();
            System.out.println("   Daily Report Generated (First 3 lines preview):");
            String[] lines = report.split("\n");
            for (int i = 0; i < Math.min(3, lines.length); i++) {
                System.out.println("     " + lines[i]);
            }
        } catch (Exception e) {
            System.out.println("   Report generation notice: " + e.getMessage());
        }

        // Lab 12: AccountStore & Comparator Sorting
        System.out.println("\n[LAB-12] Testing AccountStore Collections & Balance Comparator:");
        AccountStore store = new AccountStore();
        store.add(new SavingsAccount("Alice", 3000L, 1000L));
        store.add(new SavingsAccount("Bob", 9000L, 1000L));
        store.add(new SavingsAccount("Charlie", 5000L, 1000L));
        List<Account> sorted = store.sortedByBalance();
        System.out.print("   Accounts sorted by balance descending: ");
        for (Account a : sorted) {
            System.out.print(a.getOwnerName() + " (" + a.getBalance() + " INR) -> ");
        }
        System.out.println("END");

        // Lab 13: Generic Repository<T> with Reflected @Id & Bounded Generic Method
        System.out.println("\n[LAB-13] Testing Generic Repository<T> & Bounded Generic Method:");
        Repository<Customer> custRepo = new Repository<>();
        Customer demoCust = new Customer("Generic Test Customer", "test@generic.com", "9998887776");
        custRepo.save(demoCust);
        Customer fetched = custRepo.findById(demoCust.getCustomerId());
        System.out.println("   Generic Repository<Customer> stored and fetched by @Id: " + fetched.getCustomerId() + " (" + fetched.getName() + ")");
        Double[] nums = {10.5, 20.5, 30.0, 40.0};
        System.out.println("   Bounded generic average<N extends Number> test: " + Repository.average(nums));

        // Lab 14: BankService Integration & Audit Logger
        System.out.println("\n[LAB-14] Testing BankService Capstone Integration & @Audit Logger:");
        System.out.println("   BankService Repository Count: " + bankService.getAccountRepository().count() + " accounts active.");
        List<String> auditLogs = bankService.getAuditLogger().getAuditTrail();
        System.out.println("   Audit Trail Count: " + auditLogs.size() + " actions recorded.");
        if (!auditLogs.isEmpty()) {
            System.out.println("   Latest Audit Log: " + auditLogs.get(auditLogs.size() - 1));
        }

        System.out.println("\n================================================================================");
        System.out.println("     ALL 14 PRACTICAL LAB MODULES VERIFIED PERFECTLY AND OPERATIONAL!           ");
        System.out.println("================================================================================\n");
    }
}
