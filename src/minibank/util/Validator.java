package minibank.util;

import java.util.regex.Pattern;

/**
 * Utility class providing compiled regex validation for domain inputs (Practical 4).
 */
public final class Validator {

    // Mobile: 10 digits starting with 6, 7, 8, or 9
    private static final Pattern MOBILE_PATTERN = Pattern.compile("^[6-9]\\d{9}$");

    // Email: standard RFC-compliant basic email format
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    // PAN: 5 uppercase letters, 4 digits, 1 uppercase letter (e.g. ABCDE1234F)
    private static final Pattern PAN_PATTERN = Pattern.compile("^[A-Z]{5}[0-9]{4}[A-Z]$");

    // IFSC: 4 uppercase letters, 0, followed by 6 alphanumeric chars (e.g. SBIN0001234)
    private static final Pattern IFSC_PATTERN = Pattern.compile("^[A-Z]{4}0[A-Z0-9]{6}$");

    // Amount: positive whole number without leading zeros
    private static final Pattern AMOUNT_PATTERN = Pattern.compile("^[1-9][0-9]*$");

    private Validator() {
        // Prevent instantiation
    }

    public static boolean isValidMobile(String mobile) {
        return mobile != null && MOBILE_PATTERN.matcher(mobile.trim()).matches();
    }

    public static boolean isValidEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email.trim()).matches();
    }

    public static boolean isValidPan(String pan) {
        return pan != null && PAN_PATTERN.matcher(pan.trim().toUpperCase()).matches();
    }

    public static boolean isValidIfsc(String ifsc) {
        return ifsc != null && IFSC_PATTERN.matcher(ifsc.trim().toUpperCase()).matches();
    }

    public static boolean isValidAmount(String amountStr) {
        return amountStr != null && AMOUNT_PATTERN.matcher(amountStr.trim()).matches();
    }
}
