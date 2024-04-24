package Utils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

public class StringUtil {

    // Applies SHA256 to a string and returns the result.
    public static String applySha256(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");

            // Applies SHA256 to string input
            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder(); // This will contain hash as hexadecimal

			for (byte b : hash) {
				String hex = Integer.toHexString(0xff & b);

				if (hex.length() == 1) {
					hexString.append("0");
				}

				hexString.append(hex);
			}

            return hexString.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}