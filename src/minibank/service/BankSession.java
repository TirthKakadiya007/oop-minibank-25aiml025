package minibank.service;

import java.time.LocalDateTime;

/**
 * AutoCloseable banking session resource demonstrating try-with-resources (Practical 8).
 */
public class BankSession implements AutoCloseable {

    private final String sessionId;
    private final String operator;
    private final LocalDateTime startTime;
    private boolean open;

    public BankSession(String sessionId, String operator) {
        this.sessionId = sessionId;
        this.operator = operator;
        this.startTime = LocalDateTime.now();
        this.open = true;
        System.out.println("[SESSION OPENED] Session " + sessionId + " started by " + operator + " at " + startTime);
    }

    public String getSessionId() {
        return sessionId;
    }

    public String getOperator() {
        return operator;
    }

    public boolean isOpen() {
        return open;
    }

    @Override
    public void close() {
        this.open = false;
        System.out.println("[SESSION CLOSED] Session " + sessionId + " safely closed for " + operator + " at " + LocalDateTime.now());
    }
}
