package minibank.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Report generator utility that processes audit logs using Java NIO (Practical 11).
 * Uses Files.newDirectoryStream, BufferedReader, Files.readAttributes, and writes a summary report.
 */
public final class ReportGenerator {

    private ReportGenerator() {
        // Prevent instantiation
    }

    /**
     * Inspects the logs directory, accumulates totals, and writes an end-of-day summary report.
     * @param logsDir directory containing .log files
     * @param reportFile output summary report file path
     * @return the report content as a String
     */
    public static String generateDailyReport(Path logsDir, Path reportFile) throws IOException {
        long totalDeposits = 0L;
        long totalWithdrawals = 0L;
        long totalTransfers = 0L;
        int filesProcessed = 0;
        int linesProcessed = 0;

        StringBuilder reportSb = new StringBuilder();
        reportSb.append("================================================================\n");
        reportSb.append("              MINIBANK END-OF-DAY AUDIT & SUMMARY REPORT        \n");
        reportSb.append("================================================================\n");
        reportSb.append(String.format(" Generated: %s\n", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))));
        reportSb.append(String.format(" Logs Directory: %s\n", logsDir.toAbsolutePath()));
        reportSb.append("----------------------------------------------------------------\n");
        reportSb.append(" Processed Log Files:\n");

        if (Files.exists(logsDir) && Files.isDirectory(logsDir)) {
            // Using Files.newDirectoryStream to walk logs directory
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(logsDir)) {
                for (Path entry : stream) {
                    // Supplementary: Skip files that are empty or do not end with .log
                    String fileName = entry.getFileName().toString();
                    if (!fileName.endsWith(".log") || Files.size(entry) == 0) {
                        continue;
                    }

                    filesProcessed++;
                    BasicFileAttributes attrs = Files.readAttributes(entry, BasicFileAttributes.class);
                    reportSb.append(String.format("   * %s (Size: %d bytes, Modified: %s)\n",
                            fileName, attrs.size(), attrs.lastModifiedTime()));

                    // Read line by line with BufferedReader
                    try (BufferedReader reader = Files.newBufferedReader(entry, StandardCharsets.UTF_8)) {
                        String line;
                        while ((line = reader.readLine()) != null) {
                            line = line.trim();
                            if (line.isEmpty() || line.startsWith("#")) continue;
                            linesProcessed++;

                            // Parsing line format e.g., "DEPOSIT AC0001 500" or "[TIMESTAMP] DEPOSIT AC0001 500 ..."
                            String[] tokens = line.split("\\s+");
                            for (int i = 0; i < tokens.length - 1; i++) {
                                String t = tokens[i].toUpperCase();
                                if (t.equals("DEPOSIT")) {
                                    try {
                                        long amt = Long.parseLong(tokens[tokens.length - 1].replaceAll("[^0-9]", ""));
                                        totalDeposits += amt;
                                    } catch (Exception ignored) {}
                                    break;
                                } else if (t.equals("WITHDRAW")) {
                                    try {
                                        long amt = Long.parseLong(tokens[tokens.length - 1].replaceAll("[^0-9]", ""));
                                        totalWithdrawals += amt;
                                    } catch (Exception ignored) {}
                                    break;
                                } else if (t.equals("TRANSFER")) {
                                    try {
                                        long amt = Long.parseLong(tokens[tokens.length - 1].replaceAll("[^0-9]", ""));
                                        totalTransfers += amt;
                                    } catch (Exception ignored) {}
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }

        long netChange = totalDeposits - totalWithdrawals;

        reportSb.append("----------------------------------------------------------------\n");
        reportSb.append(String.format(" Total Log Files Evaluated  : %d\n", filesProcessed));
        reportSb.append(String.format(" Total Transactions Logged  : %d\n", linesProcessed));
        reportSb.append(String.format(" Total Deposits Amount      : +%d INR\n", totalDeposits));
        reportSb.append(String.format(" Total Withdrawals Amount   : -%d INR\n", totalWithdrawals));
        reportSb.append(String.format(" Total Transfers Volume     :  %d INR\n", totalTransfers));
        reportSb.append(String.format(" Net Liquidity Change       : %+d INR\n", netChange));
        reportSb.append("================================================================\n");

        String result = reportSb.toString();

        // Write summary report to file
        if (reportFile != null) {
            if (reportFile.getParent() != null) {
                Files.createDirectories(reportFile.getParent());
            }
            Files.writeString(reportFile, result, StandardCharsets.UTF_8);
        }

        return result;
    }
}
