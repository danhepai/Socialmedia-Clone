package org.example.mysocialnetworkgui.repository;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class PasswordHasher {
    public static String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encodedHash = digest.digest(password.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : encodedHash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.substring(0, Math.min(hexString.length(), 15));
        } catch (Exception e) {
            throw new RuntimeException("Error while hashing password", e);
        }
    }

    public static boolean verifyPassword(String inputPassword, String storedHash) {
        // Hash parola introdusă de utilizator
        String inputHash = hashPassword(inputPassword);

        // Compară hash-ul generat cu hash-ul stocat
        return inputHash.equals(storedHash);
    }
}
