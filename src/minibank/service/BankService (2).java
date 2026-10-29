package minibank.service;

import minibank.exception.AccountNotFoundException;
import minibank.exception.BankException;
import minibank.exception.InsufficientFundsException;
import minibank.exception.InvalidAmountException;
import minibank.model.Account;
import minibank.model.CurrentAccount;
import minibank.model.Customer;
import minibank.model.FixedDepositAccount;
import minibank.model.SavingsAccount;
import minibank.model.annotation.Audit;
import minibank.repository.Repository;
import minibank.util.ReportGenerator;
import minibank.util.StatePersister;
import minibank.util.StatementFormatter;
import minibank.util.TransactionLog;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Service facade providing complete business logic, persistence, and audit logging (Practical 14).
 * Coordinates generic Repository, Validator, Exception handling, NIO logging, and TransactionProcessor.
 */
public class BankService {

    private final Repository<Account> accountRepo = new Repository<>();
    private final Repository<Customer> customerRepo = new Repository<>();
    private final Map<String, List<String>> accountHistories = new ConcurrentHashMap<>();
    private final AuditLogger auditLogger = new AuditLogger();
    private final Path dataFile;
    private final Path logsDir;
    private final Path dailyLogFile;
    private final Path reportFile;

    public BankService() {
        this(Paths.get("data/accounts.dat"), Paths.get("data/logs"));
    }

    public BankService(Path dataFile, Path logsDir) {
        this.dataFile = dataFile;
        this.logsDir = logsDir;
        String dateSuffix = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        this.dailyLogFile = logsDir.resolve("transactions-" + dateSuffix + ".log");
        this.reportFile = logsDir.resolve("audit-report-" + dateSuffix + ".txt");
    }

    public Repository<Account> getAccountRepository() {
        return accountRepo;
    }

    public Repository<Customer> getCustomerRepository() {
        return customerRepo;
    }

    public AuditLogger getAuditLogger() {
        return auditLogger;
    }

    private void recordLog(String line) {
        try {
            TransactionLog.append(dailyLogFile, line);
        } catch (IOException e) {
            System.err.println("Failed to write to transaction log: " + e.getMessage());
        }
    }

    private void addHistory(String accNum, String entry) {
        accountHistories.computeIfAbsent(accNum, k -> new ArrayList<>()).add(entry);
    }

    // -------------------------------------------------------------
    // Banking Operations
    // -------------------------------------------------------------

    @Audit(action = "OPEN_ACCOUNT")
    public Account openAccount(Customer customer, String accountType, long initialDeposit, long extraParam) throws BankException {
        if (customer == null) {
            throw new IllegalArgumentException("Customer cannot be null");
        }
        if (initialDeposit < 0) {
            throw new InvalidAmountException("Initial deposit cannot be negative: " + initialDeposit);
        }

        customerRepo.save(customer);

        Account account;
        String type = accountType.trim().toUpperCase();
        switch (type) {
            case "SAVINGS", "SAVING" -> {
                long minBal = extraParam > 0 ? extraParam : 0L;
                if (initialDeposit < minBal) {
                    throw new InvalidAmountException("Opening balance must be at least minimum balance of " + minBal + " INR");
                }
                account = new SavingsAccount(customer.getName(), initialDeposit, minBal);
            }
            case "CURRENT" -> {
                long overdraft = extraParam > 0 ? extraParam : 5000L;
                account = new CurrentAccount(customer.getName(), initialDeposit, overdraft);
            }
            case "FIXED_DEPOSIT", "FD" -> {
                if (initialDeposit < 1000) {
                    throw new InvalidAmountException("Fixed Deposit requires a minimum deposit of 1000 INR");
                }
                account = new FixedDepositAccount(customer.getName(), initialDeposit);
            }
            default -> throw new IllegalArgumentException("Unknown account type: " + accountType + ". Valid types: SAVINGS, CURRENT, FIXED_DEPOSIT");
        }

        accountRepo.save(account);

        String time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        String logEntry = String.format("[%s] OPEN_ACCOUNT %s %s InitialDeposit=%d",
                time, account.getAccountNumber(), account.getClass().getSimpleName(), initialDeposit);
        recordLog(logEntry);
        addHistory(account.getAccountNumber(), logEntry);

        auditLogger.logInvocation(BankService.class, "openAccount", new Object[]{customer, accountType, initialDeposit, extraParam});
        return account;
    }

    @Audit(action = "DEPOSIT")
    public void deposit(String accountNumber, long amount) throws BankException {
        Account account = accountRepo.findById(accountNumber);
        if (account == null) {
            throw new AccountNotFoundException("Account not found with ID: " + accountNumber);
        }

        account.deposit(amount);

        String time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        String logLine = String.format("[%s] DEPOSIT %s %d", time, accountNumber, amount);
        recordLog(logLine);
        addHistory(accountNumber, logLine);

        auditLogger.logInvocation(BankService.class, "deposit", new Object[]{accountNumber, amount});
    }

    @Audit(action = "WITHDRAW")
    public void withdraw(String accountNumber, long amount) throws BankException {
        Account account = accountRepo.findById(accountNumber);
        if (account == null) {
            throw new AccountNotFoundException("Account not found with ID: " + accountNumber);
        }

        account.withdraw(amount);

        String time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        String logLine = String.format("[%s] WITHDRAW %s %d", time, accountNumber, amount);
        recordLog(logLine);
        addHistory(accountNumber, logLine);

        auditLogger.logInvocation(BankService.class, "withdraw", new Object[]{accountNumber, amount});
    }

    @Audit(action = "TRANSFER")
    public void transfer(String fromAccount, String toAccount, long amount) throws BankException {
        Account source = accountRepo.findById(fromAccount);
        if (source == null) {
            throw new AccountNotFoundException("Source account not found: " + fromAccount);
        }
        Account target = accountRepo.findById(toAccount);
        if (target == null) {
            throw new AccountNotFoundException("Target account not found: " + toAccount);
        }

        source.transfer(target, amount);

        String time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        String logLine = String.format("[%s] TRANSFER from %s to %s %d", time, fromAccount, toAccount, amount);
        recordLog(logLine);
        addHistory(fromAccount, logLine);
        addHistory(toAccount, logLine);

        auditLogger.logInvocation(BankService.class, "transfer", new Object[]{fromAccount, toAccount, amount});
    }

    public String statement(String accountNumber) throws AccountNotFoundException {
        Account account = accountRepo.findById(accountNumber);
        if (account == null) {
            throw new AccountNotFoundException("Account not found with ID: " + accountNumber);
        }
        List<String> history = accountHistories.getOrDefault(accountNumber, List.of());
        return StatementFormatter.buildStatement(account, history);
    }

    public List<Account> listAccounts() {
        return accountRepo.findAll();
    }

    public List<Account> listAccountsSortedByBalance() {
        List<Account> list = accountRepo.findAll();
        list.sort(Comparator.comparingLong(Account::getBalance).reversed());
        return list;
    }

    public List<Account> searchAccountsByOwner(String ownerName) {
        if (ownerName == null || ownerName.isBlank()) return List.of();
        String q = ownerName.trim().toLowerCase();
        List<Account> matches = new ArrayList<>();
        for (Account acc : accountRepo.findAll()) {
            if (acc.getOwnerName().toLowerCase().contains(q)) {
                matches.add(acc);
            }
        }
        return matches;
    }

    // -------------------------------------------------------------
    // Concurrency Batch Processing (Practical 10, 14)
    // -------------------------------------------------------------
    public void processBatchTransactions(List<Runnable> tasks) {
        TransactionProcessor processor = new TransactionProcessor();
        for (Account acc : accountRepo.findAll()) {
            processor.addSharedAccount(acc);
        }
        for (Runnable task : tasks) {
            processor.submit(task);
        }
        processor.stop();
    }

    // -------------------------------------------------------------
    // Reporting (Practical 11, 14)
    // -------------------------------------------------------------
    public String generateDailyReport() throws IOException {
        return ReportGenerator.generateDailyReport(logsDir, reportFile);
    }

    // -------------------------------------------------------------
    // Persistence Save / Load (Practical 11, 14)
    // -------------------------------------------------------------
    public void saveCurrentState() throws IOException {
        List<Account> accounts = accountRepo.findAll();
        StatePersister.save(accounts.toArray(new Account[0]), dataFile);
    }

    public void loadSavedState() throws IOException, ClassNotFoundException {
        Account[] accounts = StatePersister.load(dataFile);
        for (Account acc : accounts) {
            accountRepo.save(acc);
        }
    }
}
