package minibank.service;

import minibank.model.annotation.Audit;

import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Reflective audit logger (Practical 14 Optional).
 * Inspects classes for methods marked with @Audit and logs their execution.
 */
public class AuditLogger {

    private final List<String> auditTrail = new ArrayList<>();

    public void logInvocation(Class<?> targetClass, String methodName, Object[] args) {
        try {
            for (Method method : targetClass.getDeclaredMethods()) {
                if (method.getName().equals(methodName) && method.isAnnotationPresent(Audit.class)) {
                    Audit audit = method.getAnnotation(Audit.class);
                    String action = audit.action().isEmpty() ? methodName : audit.action();
                    String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                    String record = String.format("[%s] AUDIT LOG: Action '%s' invoked on %s",
                            timestamp, action, targetClass.getSimpleName());
                    auditTrail.add(record);
                    return;
                }
            }
        } catch (Exception ignored) {
        }
    }

    public List<String> getAuditTrail() {
        return Collections.unmodifiableList(auditTrail);
    }
}
