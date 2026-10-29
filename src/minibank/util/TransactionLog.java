package minibank.util;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Collections;

/**
 * Append-only transaction audit logging utility using Java NIO (Practical 11).
 */
public final class TransactionLog {

    private TransactionLog() {
        // Prevent instantiation
    }

    /**
     * Appends a log line to the specified file using CREATE and APPEND options.
     */
    public static void append(Path file, String line) throws IOException {
        if (file == null || line == null) return;
        if (file.getParent() != null) {
            Files.createDirectories(file.getParent());
        }
        Files.write(
                file,
                Collections.singletonList(line),
                StandardCharsets.UTF_8,
                StandardOpenOption.CREATE,
                StandardOpenOption.WRITE,
                StandardOpenOption.APPEND
        );
    }
}
