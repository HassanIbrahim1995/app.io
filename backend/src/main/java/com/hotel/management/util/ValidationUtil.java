package com.hotel.management.util;

import com.hotel.management.exception.BadRequestException;

import java.math.BigDecimal;
import java.util.regex.Pattern;

/**
 * Comprehensive validation utility class
 */
public class ValidationUtil {

    // Email regex pattern
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );

    // Phone regex pattern (international format)
    private static final Pattern PHONE_PATTERN = Pattern.compile(
        "^[+]?[0-9]{10,15}$"
    );

    // Password must contain: uppercase, lowercase, digit, special char
    private static final Pattern PASSWORD_PATTERN = Pattern.compile(
        "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{8,}$"
    );

    /**
     * Validate email format
     */
    public static void validateEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new BadRequestException("Email is required");
        }
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            throw new BadRequestException("Invalid email format");
        }
    }

    /**
     * Validate phone number format
     */
    public static void validatePhone(String phone) {
        if (phone == null || phone.trim().isEmpty()) {
            return; // Phone might be optional
        }
        String cleanPhone = phone.replaceAll("[\\s\\-()]", "");
        if (!PHONE_PATTERN.matcher(cleanPhone).matches()) {
            throw new BadRequestException("Invalid phone number format");
        }
    }

    /**
     * Validate password strength
     */
    public static void validatePassword(String password) {
        if (password == null || password.isEmpty()) {
            throw new BadRequestException("Password is required");
        }
        if (password.length() < 8) {
            throw new BadRequestException("Password must be at least 8 characters long");
        }
        if (!PASSWORD_PATTERN.matcher(password).matches()) {
            throw new BadRequestException(
                "Password must contain at least one uppercase letter, one lowercase letter, " +
                "one digit, and one special character"
            );
        }
    }

    /**
     * Validate positive number
     */
    public static void validatePositiveNumber(BigDecimal value, String fieldName) {
        if (value == null) {
            throw new BadRequestException(fieldName + " is required");
        }
        if (value.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BadRequestException(fieldName + " must be positive");
        }
    }

    /**
     * Validate non-negative number
     */
    public static void validateNonNegativeNumber(BigDecimal value, String fieldName) {
        if (value == null) {
            throw new BadRequestException(fieldName + " is required");
        }
        if (value.compareTo(BigDecimal.ZERO) < 0) {
            throw new BadRequestException(fieldName + " must be non-negative");
        }
    }

    /**
     * Validate integer range
     */
    public static void validateIntegerRange(Integer value, int min, int max, String fieldName) {
        if (value == null) {
            throw new BadRequestException(fieldName + " is required");
        }
        if (value < min || value > max) {
            throw new BadRequestException(
                fieldName + " must be between " + min + " and " + max
            );
        }
    }

    /**
     * Validate string length
     */
    public static void validateStringLength(String value, int min, int max, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new BadRequestException(fieldName + " is required");
        }
        int length = value.trim().length();
        if (length < min || length > max) {
            throw new BadRequestException(
                fieldName + " must be between " + min + " and " + max + " characters"
            );
        }
    }

    /**
     * Validate not null
     */
    public static void validateNotNull(Object value, String fieldName) {
        if (value == null) {
            throw new BadRequestException(fieldName + " is required");
        }
    }

    /**
     * Validate room number format
     */
    public static void validateRoomNumber(String roomNumber) {
        if (roomNumber == null || roomNumber.trim().isEmpty()) {
            throw new BadRequestException("Room number is required");
        }
        // Room number should be alphanumeric
        if (!roomNumber.matches("^[A-Za-z0-9-]+$")) {
            throw new BadRequestException("Invalid room number format");
        }
    }

    /**
     * Validate credit card number (basic Luhn algorithm)
     */
    public static boolean isValidCreditCard(String cardNumber) {
        if (cardNumber == null || cardNumber.isEmpty()) {
            return false;
        }
        
        String cleaned = cardNumber.replaceAll("\\s+", "");
        if (!cleaned.matches("^[0-9]{13,19}$")) {
            return false;
        }

        // Luhn algorithm
        int sum = 0;
        boolean alternate = false;
        for (int i = cleaned.length() - 1; i >= 0; i--) {
            int n = Integer.parseInt(cleaned.substring(i, i + 1));
            if (alternate) {
                n *= 2;
                if (n > 9) {
                    n = (n % 10) + 1;
                }
            }
            sum += n;
            alternate = !alternate;
        }
        return (sum % 10 == 0);
    }

    /**
     * Validate percentage value (0-100)
     */
    public static void validatePercentage(BigDecimal percentage, String fieldName) {
        if (percentage == null) {
            throw new BadRequestException(fieldName + " is required");
        }
        if (percentage.compareTo(BigDecimal.ZERO) < 0 || 
            percentage.compareTo(BigDecimal.valueOf(100)) > 0) {
            throw new BadRequestException(fieldName + " must be between 0 and 100");
        }
    }

    /**
     * Validate guest count for room capacity
     */
    public static void validateGuestCount(Integer guestCount, Integer maxOccupancy) {
        if (guestCount == null || guestCount <= 0) {
            throw new BadRequestException("Guest count must be at least 1");
        }
        if (maxOccupancy != null && guestCount > maxOccupancy) {
            throw new BadRequestException(
                "Guest count (" + guestCount + ") exceeds room capacity (" + maxOccupancy + ")"
            );
        }
    }
}
