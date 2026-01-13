package swiftx.crypto;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;

/**
 * Utility class for cryptographic operations.
 *
 * <p>For full design rationale, see:
 * <a href="/Security.md">Security Architecture & Cryptography Concepts</a>
 *
 * <p>Threat model and trade-offs are documented there.
 */
public class CryptoUtils {
    /* Here we define -
       1.) Encryption Algorithm
       2.) Block Mode
       3.) Padding
     */
    private static final String TRANSFORMATION_STRING = "AES/CBC/PKCS5Padding";

    // Getting the key
    public static SecretKey getKey(byte[] keyBytes) {
        return new SecretKeySpec(keyBytes, "AES");
    }

    // IV Generation
    public static byte[] generateIV(){
        byte[] iv = new byte[16]; // Block Size (AES
        SecureRandom secureRandom = new SecureRandom();
        secureRandom.nextBytes(iv);
        return iv;
    }

    public static Cipher initEncryptCipher(SecretKey key, byte[] iv) throws Exception {
        Cipher cipher = Cipher.getInstance(TRANSFORMATION_STRING);
        cipher.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(iv));
        return cipher;
    }

    public static Cipher initDecryptCipher(SecretKey key, byte[] iv) throws Exception {
        Cipher cipher = Cipher.getInstance(TRANSFORMATION_STRING);
        cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(iv));
        return cipher;
    }
}
