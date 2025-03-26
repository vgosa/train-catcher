package org.group21.user.util;

import org.group21.user.exception.RuntimeHashException;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class PasswordUtil {
    /**
     * Hashing helper method to hash the password using SHA-256
     * @implNote WARNING: THIS METHOD IS NOT SECURE FOR PRODUCTION USE. IT DOES NOT USE RANDOM SALTING!
     * @param email User email
     * @param password User password
     * @return Hashed password
     */
    public static String hashPassword(String email, String password) {
        String combined = email + password;
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(combined.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeHashException("Error generating password hash", e);
        }
    }
}
