package com.passwordmanager.crypto;

import javax.crypto.*;
import javax.crypto.spec.*;
import java.security.*;
import java.security.spec.*;
import java.util.Arrays;

public final class CryptoService {

    public static final int SALT_LENGTH = 16;
    private static final int IV_LENGTH = 12;
    private static final int GCM_TAG_BITS = 128;
    private static final int KEY_BITS = 256;
    private static final int PBKDF2_ITERATIONS = 600_000;
    private static final String KEY_ALGORITHM = "AES";
    private static final String CIPHER_ALGORITHM = "AES/GCM/NoPadding";
    private static final String KDF_ALGORITHM = "PBKDF2WithHmacSHA256";

    private final SecretKey key;
    private final byte[] salt;

    private CryptoService(SecretKey key, byte[] salt) {
        this.key = key;
        this.salt = salt;
    }

    /**
     * Creates a new CryptoService with a freshly generated random salt.
     * Use for first-run vault creation.
     */
    public static CryptoService withNewSalt(char[] password) throws GeneralSecurityException {
        byte[] salt = new byte[SALT_LENGTH];
        new SecureRandom().nextBytes(salt);
        return new CryptoService(deriveKey(password, salt), salt);
    }

    /**
     * Creates a CryptoService using an existing salt (read from the file header).
     * Use when opening an existing vault.
     */
    public static CryptoService withSalt(char[] password, byte[] salt) throws GeneralSecurityException {
        byte[] saltCopy = Arrays.copyOf(salt, SALT_LENGTH);
        return new CryptoService(deriveKey(password, saltCopy), saltCopy);
    }

    /**
     * Encrypts plaintext. Returns {@code [salt(16)][IV(12)][ciphertext + GCM tag]}.
     * A fresh random IV is generated on every call.
     */
    public byte[] encrypt(byte[] plaintext) throws GeneralSecurityException {
        byte[] iv = new byte[IV_LENGTH];
        new SecureRandom().nextBytes(iv);

        Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, key, new GCMParameterSpec(GCM_TAG_BITS, iv));
        byte[] ciphertext = cipher.doFinal(plaintext);

        byte[] result = new byte[SALT_LENGTH + IV_LENGTH + ciphertext.length];
        System.arraycopy(salt, 0, result, 0, SALT_LENGTH);
        System.arraycopy(iv, 0, result, SALT_LENGTH, IV_LENGTH);
        System.arraycopy(ciphertext, 0, result, SALT_LENGTH + IV_LENGTH, ciphertext.length);
        return result;
    }

    /**
     * Decrypts data produced by {@link #encrypt(byte[])}.
     * Throws {@link AEADBadTagException} if the key is wrong or data is corrupted.
     */
    public byte[] decrypt(byte[] data) throws GeneralSecurityException {
        if (data.length < SALT_LENGTH + IV_LENGTH) {
            throw new IllegalArgumentException("Encrypted data is too short to be valid");
        }
        byte[] iv = Arrays.copyOfRange(data, SALT_LENGTH, SALT_LENGTH + IV_LENGTH);
        byte[] ciphertext = Arrays.copyOfRange(data, SALT_LENGTH + IV_LENGTH, data.length);

        Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, key, new GCMParameterSpec(GCM_TAG_BITS, iv));
        return cipher.doFinal(ciphertext);
    }

    private static SecretKey deriveKey(char[] password, byte[] salt) throws GeneralSecurityException {
        PBEKeySpec spec = new PBEKeySpec(password, salt, PBKDF2_ITERATIONS, KEY_BITS);
        try {
            SecretKeyFactory factory = SecretKeyFactory.getInstance(KDF_ALGORITHM);
            byte[] keyBytes = factory.generateSecret(spec).getEncoded();
            return new SecretKeySpec(keyBytes, KEY_ALGORITHM);
        } finally {
            spec.clearPassword();
        }
    }
}
