# MiniBank — Object Oriented Programming Capstone Project
**Course**: Object Oriented Programming (`CEUE203`) | **A.Y.**: 2026 – 27 | **Semester**: 3  
**Repository Name**: `oop-minibank-25aiml025`

---

## 🌟 Executive Summary

**MiniBank** is an enterprise-grade, console-based banking application developed progressively across 14 laboratory sessions. It demonstrates core and advanced Object-Oriented Programming (OOP) paradigms in modern Java (JDK 17+), integrating:

- **Encapsulated Data Models & Records** (`BankInfo`, `Customer`, `Account`, `Command`)
- **Polymorphism & Class Hierarchies** (`SavingsAccount`, `CurrentAccount`, `FixedDepositAccount`)
- **Interfaces & Lambdas** (`Transactable`, `InterestBearing`, `WithdrawRule`, `Premium`)
- **Custom Metadata & Runtime Reflection** (`@Id`, `@Positive`, `@MaxLength`, `@Audit`, `AnnotationValidator`)
- **Robust Exception Hierarchies** (`BankException`, `InsufficientFundsException` with shortfall tracking)
- **Multi-threading & Thread Safety** (Race condition prevention via `synchronized` methods, `AccountWorker`)
- **Enterprise Concurrency Engine** (`TransactionProcessor`, `ExecutorService`, Deadlock Prevention via consistent lock ordering, Producer-Consumer buffer with `wait()` / `notify()`)
- **Object Serialization & NIO Log Processing** (`StatePersister`, `TransactionLog`, `ReportGenerator` with `DirectoryStream`)
- **Collections Framework & Sorting** (`HashMap`, `ArrayList`, `Comparator`, `Comparable`, `ConcurrentHashMap`)
- **Generic Data Access Layer** (`Repository<T>` with reflected `@Id` resolution, `@SafeVarargs`, bounded generic parameters `<N extends Number>`)
- **Layered Architecture & Comprehensive JUnit 5 Test Suite** (`BankService`, `MiniBankTest`)

---

## 📁 Project Structure

```
oop-minibank/
├── .gitignore                      # Git ignore file for binaries and runtime data
├── README.md                       # Comprehensive project documentation & viva guide
├── build.sh / build.bat            # Build automation script (compiles & packages JAR)
├── run.sh / run.bat                # Execution script
├── test.sh / test.bat              # JUnit 5 test execution script
├── minibank.jar                    # Final runnable application JAR
├── lib/
│   └── junit-platform-console-standalone.jar  # Zero-dependency JUnit 5 runner
├── src/
│   └── minibank/
│       ├── MiniBank.java           # Main interactive entry point & Lab Milestone Demo
│       ├── model/
│       │   ├── BankInfo.java       # Record for bank header info (Lab 1)
│       │   ├── MenuOption.java     # Navigation enum (Lab 1 & 14)
│       │   ├── TransactionType.java# Enum: DEPOSIT, WITHDRAW, TRANSFER (Lab 4)
│       │   ├── Command.java        # Record for parsed CLI commands (Lab 4)
│       │   ├── Customer.java       # Customer entity with static ID counter & clone() (Lab 2, 3, 11)
│       │   ├── Account.java        # Abstract base account (Lab 2, 3, 5, 6, 7, 8, 9, 11, 12)
│       │   ├── SavingsAccount.java # Savings account with minBalance & 4% interest (Lab 5)
│       │   ├── CurrentAccount.java # Current account with overdraft & 0% interest (Lab 5)
│       │   ├── FixedDepositAccount.java # FD account with 7% interest (Lab 5)
│       │   ├── Transactable.java   # Interface for deposit/withdraw (Lab 6)
│       │   ├── InterestBearing.java# Interface with default interest calculation (Lab 6)
│       │   ├── WithdrawRule.java   # @FunctionalInterface for lambda withdrawal rules (Lab 6, 7)
│       │   ├── Premium.java        # Marker interface (Lab 6)
│       │   └── annotation/
│       │       ├── Id.java         # @Id marker for entity keys (Lab 7, 13)
│       │       ├── Positive.java   # @Positive numeric validator (Lab 7)
│       │       ├── MaxLength.java  # @MaxLength string validator (Lab 7)
│       │       └── Audit.java      # @Audit reflective method annotation (Lab 14)
│       ├── exception/
│       │   ├── BankException.java  # Base checked exception (Lab 8)
│       │   ├── InsufficientFundsException.java # Carries shortfall amount (Lab 8)
│       │   ├── AccountNotFoundException.java   # Unknown account ID (Lab 8)
│       │   ├── InvalidAmountException.java     # Negative/zero amount error (Lab 8)
│       │   └── DailyLimitExceededException.java# Supplementary limit exception (Lab 8)
│       ├── repository/
│       │   ├── Repository.java     # Generic Repository<T> using reflection on @Id (Lab 13)
│       │   └── AccountStore.java   # Collections store with Comparator sorting (Lab 12)
│       ├── service/
│       │   ├── BankService.java    # Complete service layer facade (Lab 14)
│       │   ├── AuditLogger.java    # Reflective logger for @Audit methods (Lab 14)
│       │   ├── BankSession.java    # AutoCloseable session for try-with-resources (Lab 8)
│       │   ├── AccountWorker.java  # Runnable worker demonstrating concurrency (Lab 9)
│       │   └── TransactionProcessor.java # Managed thread pool & deadlock-free engine (Lab 10, 12)
│       └── util/
│           ├── Validator.java      # Regex validation (Mobile, Email, PAN, IFSC) (Lab 4)
│           ├── CommandParser.java  # CLI command tokeniser and parser (Lab 4)
│           ├── StatementFormatter.java # Multi-line StringBuilder statement builder (Lab 4)
│           ├── AnnotationValidator.java# Reflection validator for annotations (Lab 7)
│           ├── StatePersister.java # Object serialization save/load (Lab 11)
│           ├── TransactionLog.java # NIO append-only log recorder (Lab 11)
│           └── ReportGenerator.java# NIO DirectoryStream audit report generator (Lab 11)
├── test/
│   └── minibank/
│       └── MiniBankTest.java       # JUnit 5 test suite (Lab 14)
└── data/                           # Generated persistent state and transaction logs
    ├── accounts.dat
    └── logs/
```

---

## 🧭 Laboratory Traceability Matrix (Part-B Complete)

| Practical | Milestone / Concept | Implemented Components & Key Features |
|---|---|---|
| **Lab 01** | Application Shell, Enum & Records | `BankInfo.java` (record), `MenuOption.java` (enum), `MiniBank.java` (menu loop with switch expression, Scanner input, re-prompting). |
| **Lab 02** | Encapsulation & Static ID Counters | `Customer.java` (static `customerCounter`, `generateCustomerId()`), `Account.java` (static `accountCounter`, constructors chaining with `this()`, encapsulated `deposit()` / `withdraw()`). |
| **Lab 03** | Object Methods & Nested Classes | `Account.java` (`toString()`, `equals()`, `hashCode()`), `Customer.Address` (static nested class), `Customer.clone()` (deep clone via `Cloneable`), `instanceof` testing. |
| **Lab 04** | Regex Validation & Token Parsing | `Validator.java` (Pattern regex for Mobile, Email, PAN, IFSC), `Command.java` (record), `CommandParser.java` (`split()` parsing), `StatementFormatter.java` (`StringBuilder`). |
| **Lab 05** | Abstract Classes & Polymorphism | Abstract `Account.java` (`interestRate()`, `canWithdraw()`), `SavingsAccount` (4.0% rate, minBalance), `CurrentAccount` (0.0% rate, overdraftLimit), `FixedDepositAccount` (7.0% rate). |
| **Lab 06** | Interfaces, Default Methods & JAR | `Transactable`, `InterestBearing` (default `yearlyInterest()`, `monthlyInterest()`), `WithdrawRule` (functional interface + lambda), `Premium` (marker), modular packaging, `minibank.jar`. |
| **Lab 07** | Custom Annotations & Reflection | `@Id`, `@Positive`, `@MaxLength`, `AnnotationValidator.java` (inspects declared fields by reflection), `@Override`, `@FunctionalInterface`. |
| **Lab 08** | Exception Hierarchy & Shortfall | `BankException`, `InsufficientFundsException` (stores `shortfall`), `AccountNotFoundException`, `InvalidAmountException`, try-catch-finally, `BankSession` (`AutoCloseable`). |
| **Lab 09** | Multithreading & Synchronization | `AccountWorker.java` (`Runnable`), `synchronized` methods on `Account` preventing race conditions across concurrent threads. |
| **Lab 10** | ThreadPools, Deadlocks & Producer-Consumer | `TransactionProcessor.java` (`ExecutorService` pool of 4), consistent lock ordering on account numbers preventing deadlocks, `TransactionQueue` with `wait()` / `notifyAll()`. |
| **Lab 11** | Object Serialization & NIO Reporting | `StatePersister.java` (`ObjectOutputStream`/`InputStream`), `TransactionLog.java` (`Files.write` with `CREATE`/`APPEND`), `ReportGenerator.java` (`Files.newDirectoryStream`, `BufferedReader`). |
| **Lab 12** | Collections Framework & Sorting | `AccountStore.java` (`HashMap`, `ArrayList`), `Account.compareTo()` (`Comparable`), `sortedByBalance()` (`Comparator`), `ConcurrentHashMap` in processor. |
| **Lab 13** | Generic Repository & Bounded Methods | `Repository<T>` (generic CRUD with reflection extracting `@Id`), `@SafeVarargs saveAll()`, bounded generic method `<N extends Number> double average(N[])`. |
| **Lab 14** | Capstone Integration & JUnit 5 Testing | `BankService.java` (wires all layers), `@Audit` reflective logging, `MiniBankTest.java` (passing JUnit 5 tests), persistent state across runs, full documentation. |

---

## 🚀 How to Build and Run

### Prerequisites
- **Java Development Kit (JDK 17 or higher)** (`javac` and `java` available in PATH).
- **Git** for version control.

### Option 1: Using Automation Scripts (Recommended)

#### On macOS / Linux:
```bash
# 1. Build and package minibank.jar
./build.sh

# 2. Run the interactive MiniBank application
./run.sh

# 3. Run all JUnit 5 unit tests
./test.sh
```

#### On Windows:
```cmd
:: 1. Build and package minibank.jar
build.bat

:: 2. Run the interactive MiniBank application
run.bat

:: 3. Run all JUnit 5 unit tests
test.bat
```

### Option 2: Using Standard JDK Command Line

#### 1. Compile the Source Code:
```bash
mkdir -p bin
javac -d bin $(find src -name "*.java")
```

#### 2. Package the Runnable JAR:
```bash
jar --create --file minibank.jar --main-class minibank.MiniBank -C bin .
```

#### 3. Run the Application:
```bash
java -jar minibank.jar
```

#### 4. Run the JUnit 5 Test Suite:
```bash
mkdir -p bin-test
javac -cp "bin:lib/junit-platform-console-standalone.jar" -d bin-test $(find test -name "*.java")
java -jar lib/junit-platform-console-standalone.jar execute -cp "bin:bin-test" --select-class minibank.MiniBankTest
```

---

## 🧭 Console Menu

The interactive application deliberately presents nine options only. Options 1–8 provide
the account operations and batch-processing demonstration; option 9 saves the current
state and exits. The automated checks are run with `test.bat` or `test.sh`, rather than
from the application menu.

---

## 📚 Viva Voce & Key Concept Review (All Practicals)

### Lab 1: Java Basics & JVM
1. **What is the role of the JVM, and what file does `javac` produce?**  
   The JVM (Java Virtual Machine) executes compiled Java bytecode on the host OS, enabling Java's "Write Once, Run Anywhere" (WORA) capability. `javac` compiles `.java` source files into platform-independent `.class` bytecode files.
2. **How does a switch expression differ from a traditional switch statement?**  
   A switch expression returns a value, uses lambda-style arrow syntax (`->`) that avoids fall-through without needing explicit `break` statements, and enforces compile-time exhaustiveness.
3. **Why use an enum for menu options and a record for read-only data?**  
   An `enum` defines a type-safe, closed set of named constants. A `record` is a compact, immutable data carrier where the compiler auto-generates constructor, accessors, `equals()`, `hashCode()`, and `toString()`.

### Lab 2: Encapsulation & Members
1. **What is encapsulation, and how do private fields with public getters enforce it?**  
   Encapsulation bundles data with the methods that operate on it and restricts direct access to internal state. Private fields prevent external mutation, while getters allow controlled read access and methods (`deposit`, `withdraw`) validate state transitions.
2. **Why is `accountNumber` declared `final`?**  
   `final` guarantees that once an account number is assigned during instantiation, its reference cannot be reassigned, preserving entity identity integrity.
3. **Difference between static and instance fields when generating IDs?**  
   A static field belongs to the class and is shared across all instances, maintaining a single global sequence counter (`accountCounter`). An instance field belongs to an individual object.

### Lab 3: Object Methods & Nested Classes
1. **Why must `equals()` and `hashCode()` be overridden together?**  
   To honor the Java contract: if two objects are equal according to `equals()`, they **must** return the same integer from `hashCode()`. Failing to do so causes hash-based collections (`HashMap`, `HashSet`) to misplace or duplicate items.
2. **Difference between a static nested class and an inner class?**  
   A `static nested class` does not hold a reference to an enclosing outer class instance, avoiding memory leaks and allowing standalone instantiation (`new Customer.Address(...)`). An inner class (non-static) requires an enclosing outer instance.

### Lab 4: Strings, Regular Expressions & StringBuilder
1. **Difference between `String`, `StringBuilder`, and `StringBuffer`?**  
   - `String`: Immutable; string concatenation creates new intermediate objects.
   - `StringBuilder`: Mutable and high-performance; non-synchronized (ideal for single-threaded assembly).
   - `StringBuffer`: Mutable and thread-safe via internal synchronization (higher overhead).
2. **What does `^[6-9][0-9]{9}$` match and why use anchors?**  
   It matches a 10-digit Indian mobile number starting with 6, 7, 8, or 9. Anchors (`^` for start of line and `$` for end of line) ensure the entire input matches, preventing substring false positives.

### Lab 5: Inheritance & Polymorphism
1. **Difference between an abstract class and a concrete class?**  
   An abstract class cannot be instantiated directly and can define abstract method signatures that subclasses must implement. Concrete classes provide full implementations and can be instantiated.
2. **What is dynamic method dispatch (run-time polymorphism)?**  
   The mechanism where a call to an overridden method is resolved at runtime based on the actual type of the object being referenced, rather than the reference type.
3. **What does `super()` do in a constructor?**  
   It invokes the constructor of the direct parent class, ensuring inherited private state is correctly initialized before subclass initialization begins.

### Lab 6: Interfaces & Default Methods
1. **Difference between an interface and an abstract class?**  
   A class can implement multiple interfaces but extend only one class. Interfaces define contracts and default/static behaviors without maintaining instance state (fields are implicitly `public static final`).
2. **What is a functional interface and how does a lambda use it?**  
   An interface with exactly one abstract method (annotated `@FunctionalInterface`). A lambda provides a concise, inline implementation of that single method.
3. **Why are default methods useful in an interface?**  
   They permit interface evolution by adding concrete methods with default implementations without breaking existing implementing classes.

### Lab 7: Custom Annotations & Reflection
1. **What are `@Retention` and `@Target` used for?**  
   - `@Retention`: Specifies how long the annotation is retained (`RUNTIME` allows inspection via reflection).
   - `@Target`: Specifies the syntactic elements (e.g. `FIELD`, `METHOD`, `TYPE`) where the annotation can be applied.
2. **What is reflection and what does `getDeclaredFields()` return?**  
   Reflection is the runtime ability to inspect and modify classes, methods, and fields. `getDeclaredFields()` returns an array of all fields declared directly by that class, regardless of access modifiers.

### Lab 8: Exception Handling
1. **Difference between checked and unchecked exceptions?**  
   - Checked (`extends Exception`): Checked at compile time; methods must declare them with `throws` or handle them with `try-catch`.
   - Unchecked (`extends RuntimeException`): Indicates programming or logic errors; not enforced at compile time.
2. **What is the purpose of `finally` and how does try-with-resources work?**  
   `finally` always executes regardless of whether an exception occurred. Try-with-resources automatically invokes `.close()` on any resource implementing `AutoCloseable` upon exiting the block, even if an exception occurs.

### Lab 9 & 10: Concurrency, ThreadPools & Deadlocks
1. **Why is a thread pool better than creating new threads per task?**  
   Thread creation is expensive. A pool reuses a fixed set of worker threads, bounds CPU/memory consumption, and manages execution queues cleanly.
2. **What causes a deadlock and how does consistent lock ordering prevent it?**  
   Deadlock occurs when two or more threads wait indefinitely for locks held by each other (circular wait). Consistent lock ordering (e.g. always acquiring the lock on the lower `accountNumber` first) eliminates circular wait conditions, guaranteeing safe multi-resource acquisition.

### Lab 11: Persistence & NIO
1. **Why implement `Serializable` and specify `serialVersionUID`?**  
   It marks a class as serializable to byte streams. `serialVersionUID` guarantees version consistency between the serialized byte stream and the runtime class during deserialization.
2. **Difference between byte streams and character streams?**  
   Byte streams (`InputStream`/`OutputStream`) handle raw 8-bit binary data (such as objects or images). Character streams (`Reader`/`Writer`) handle 16-bit Unicode text with character encoding.

### Lab 12: Collections Framework
1. **Difference between `List`, `Set`, and `Map`?**  
   - `List`: Ordered collection that allows duplicate elements.
   - `Set`: Unordered collection containing unique elements only.
   - `Map`: Key-value pairs with unique keys.
2. **Difference between `Comparable` and `Comparator`?**  
   `Comparable` defines the natural ordering of a class (`compareTo()`), whereas `Comparator` defines external, custom ordering strategies (`compare()`).

### Lab 13: Generics
1. **What is type erasure?**  
   The Java compiler replaces all generic type parameters with their bounds (or `Object`) at compile time and inserts appropriate casts, ensuring binary compatibility with older Java versions.
2. **What is a bounded type parameter (`<N extends Number>`)?**  
   It restricts the allowed types to subclasses of a given type, enabling access to methods defined on the bound (such as `.doubleValue()`).

### Lab 14: Layered Architecture
1. **What is a layered architecture and why is it useful?**  
   Separating concerns into distinct layers (Model, Repository, Service, UI) promotes modularity, testability, maintainability, and clean decoupling of business logic from persistence and presentation.

---
