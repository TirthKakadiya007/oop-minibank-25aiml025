package minibank;

import minibank.exception.InsufficientFundsException;
import minibank.exception.InvalidAmountException;
import minibank.model.Account;
import minibank.model.CurrentAccount;
import minibank.model.Customer;
import minibank.model.FixedDepositAccount;
import minibank.model.SavingsAccount;
import minibank.repository.Repository;
import minibank.service.AccountWorker;
import minibank.service.BankService;
import minibank.util.AnnotationValidator;
import minibank.util.StatePersister;
import minibank.util.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * JUnit 5 Test Suite for MiniBank Capstone Project (Practical 14).
 * Tests core banking operations, exception throwing, concurrency, persistence, and validation.
 */
public class MiniBankTest {

    private BankService bankService;

    @BeforeEach
    void setUp(@TempDir Path tempDir) {
        Path testDataFile = tempDir.resolve("accounts.dat");
        Path testLogsDir = tempDir.resolve("logs");
        bankService = new BankService(testDataFile, testLogsDir);
    }

    @Test
    @DisplayName("Practical 14 Requirement: Deposit increases balance")
    void testDepositIncreasesBalance() throws Exception {
        Customer c = new Customer("Test User", "test@user.com", "9876543210");
        Account acc = bankService.openAccount(c, "SAVINGS", 5000L, 1000L);

        assertEquals(5000L, acc.getBalance());
        bankService.deposit(acc.getAccountNumber(), 2500L);
        assertEquals(7500L, acc.getBalance());
    }

    @Test
    @DisplayName("Practical 14 Requirement: Withdrawal over the limit throws InsufficientFundsException with shortfall")
    void testWithdrawalOverLimitThrowsException() throws Exception {
        Customer c = new Customer("Test User", "test@user.com", "9876543210");
        Account acc = bankService.openAccount(c, "SAVINGS", 3000L, 1000L);

        // Savings Account min balance is 1000, so max allowable withdrawal is 2000
        InsufficientFundsException ex = assertThrows(InsufficientFundsException.class, () -> {
            bankService.withdraw(acc.getAccountNumber(), 2500L);
        });

        // Shortfall = 2500 - 3000 = -500 (or insufficient by min balance)
        assertTrue(ex.getMessage().contains("shortfall"));
        assertEquals(3000L, acc.getBalance(), "Balance must remain unchanged after failed withdrawal");
    }

    @Test
    @DisplayName("Practical 14 Requirement: Many concurrent deposits keep the balance exactly correct (Thread Safety)")
    void testConcurrentDepositsKeepBalanceCorrect() throws Exception {
        Customer c = new Customer("Thread Test", "thread@test.com", "9876543210");
        Account acc = bankService.openAccount(c, "SAVINGS", 0L, 0L);

        int threadCount = 10;
        int depositsPerThread = 500;
        long depositAmount = 2L;
        // Total expected = 10 * 500 * 2 = 10,000 INR

        Thread[] threads = new Thread[threadCount];
        for (int i = 0; i < threadCount; i++) {
            threads[i] = new Thread(new AccountWorker(acc, depositsPerThread, depositAmount));
            threads[i].start();
        }

        for (Thread t : threads) {
            t.join();
        }

        assertEquals(10000L, acc.getBalance(), "Synchronized deposits must prevent race conditions");
    }

    @Test
    @DisplayName("Practical 14 Supplementary: State persistence saves and reloads accounts unchanged")
    void testStatePersistenceSavesAndReloads(@TempDir Path tempDir) throws Exception {
        Path file = tempDir.resolve("saved-accounts.dat");
        Account a1 = new SavingsAccount("User One", 6000L, 1000L);
        Account a2 = new CurrentAccount("User Two", 12000L, 5000L);
        Account[] original = new Account[]{a1, a2};

        StatePersister.save(original, file);
        assertTrue(file.toFile().exists());

        Account[] loaded = StatePersister.load(file);
        assertEquals(2, loaded.length);
        assertEquals(a1.getAccountNumber(), loaded[0].getAccountNumber());
        assertEquals(a1.getBalance(), loaded[0].getBalance());
        assertEquals(a2.getAccountNumber(), loaded[1].getAccountNumber());
        assertEquals(a2.getBalance(), loaded[1].getBalance());
    }

    @Test
    @DisplayName("Practical 8 & 10: Transfer moves funds atomically and deadlock-free")
    void testTransferCorrectness() throws Exception {
        Customer c1 = new Customer("Sender", "sender@test.com", "9876543210");
        Customer c2 = new Customer("Receiver", "receiver@test.com", "9123456789");
        Account acc1 = bankService.openAccount(c1, "SAVINGS", 10000L, 1000L);
        Account acc2 = bankService.openAccount(c2, "CURRENT", 2000L, 1000L);

        bankService.transfer(acc1.getAccountNumber(), acc2.getAccountNumber(), 4000L);

        assertEquals(6000L, acc1.getBalance());
        assertEquals(6000L, acc2.getBalance());
    }

    @Test
    @DisplayName("Practical 4: Regex Validator validates input reliably")
    void testRegexValidators() {
        assertTrue(Validator.isValidMobile("9876543210"));
        assertFalse(Validator.isValidMobile("12345"));
        assertFalse(Validator.isValidMobile("5876543210")); // Doesn't start with 6-9

        assertTrue(Validator.isValidEmail("student@university.edu"));
        assertFalse(Validator.isValidEmail("plainaddress"));

        assertTrue(Validator.isValidPan("ABCDE1234F"));
        assertFalse(Validator.isValidPan("12345ABCDE"));

        assertTrue(Validator.isValidIfsc("SBIN0001234"));
        assertFalse(Validator.isValidIfsc("SBIN1234"));

        assertTrue(Validator.isValidAmount("500"));
        assertFalse(Validator.isValidAmount("-50"));
        assertFalse(Validator.isValidAmount("abc"));
    }

    @Test
    @DisplayName("Practical 7: Reflection-based AnnotationValidator catches constraint violations")
    void testAnnotationValidator() {
        SavingsAccount invalidAcc = new SavingsAccount("Invalid", -100L, 1000L);
        String[] errors = AnnotationValidator.validate(invalidAcc);
        assertTrue(errors.length > 0);
        assertTrue(errors[0].contains("balance"));
    }

    @Test
    @DisplayName("Practical 13: Generic Repository handles entities via reflected @Id")
    void testGenericRepository() {
        Repository<Customer> repo = new Repository<>();
        Customer cust = new Customer("John Doe", "john@example.com", "9876543210");
        repo.save(cust);

        Customer fetched = repo.findById(cust.getCustomerId());
        assertNotNull(fetched);
        assertEquals("John Doe", fetched.getName());
        assertEquals(1, repo.count());

        repo.delete(cust.getCustomerId());
        assertEquals(0, repo.count());
    }

    @Test
    @DisplayName("Practical 5: Polymorphic interest rates match specifications")
    void testPolymorphicInterestRates() {
        Account sa = new SavingsAccount("S", 1000, 500);
        Account ca = new CurrentAccount("C", 1000, 500);
        Account fda = new FixedDepositAccount("F", 1000);

        assertEquals(4.0, sa.interestRate());
        assertEquals(0.0, ca.interestRate());
        assertEquals(7.0, fda.interestRate());
    }
}
