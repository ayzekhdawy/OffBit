package com.offbit.offbit.utils;

import android.content.Context;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.util.Base64;
import android.util.Log;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.AlgorithmParameterSpec;

import javax.crypto.Cipher;

public class SecurityUtils {
    private static final String TAG = "SecurityUtils";
    private static final String ANDROID_KEYSTORE = "AndroidKeyStore";
    private static final String KEY_ALIAS = "offbit_p2p_key";

    /**
     * Generate a key pair and store it in Android Keystore
     */
    public static KeyPair generateAndStoreKeyPair(Context context) {
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(
                    KeyProperties.KEY_ALGORITHM_RSA, ANDROID_KEYSTORE);

            AlgorithmParameterSpec spec = new KeyGenParameterSpec.Builder(
                    KEY_ALIAS,
                    KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                    .setDigests(KeyProperties.DIGEST_SHA256, KeyProperties.DIGEST_SHA512)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_RSA_PKCS1)
                    .setKeySize(2048)
                    .build();

            keyPairGenerator.initialize(spec);
            return keyPairGenerator.generateKeyPair();

        } catch (Exception e) {
            Log.e(TAG, "Error generating key pair", e);
            return null;
        }
    }

    /**
     * Retrieve stored key pair from Android Keystore
     */
    public static KeyPair retrieveStoredKeyPair() {
        try {
            KeyStore keyStore = KeyStore.getInstance(ANDROID_KEYSTORE);
            keyStore.load(null);

            if (!keyStore.containsAlias(KEY_ALIAS)) {
                return null;
            }

            KeyStore.PrivateKeyEntry privateKeyEntry = (KeyStore.PrivateKeyEntry) keyStore
                    .getEntry(KEY_ALIAS, null);

            if (privateKeyEntry == null) {
                return null;
            }

            PrivateKey privateKey = privateKeyEntry.getPrivateKey();
            PublicKey publicKey = privateKeyEntry.getCertificate().getPublicKey();

            return new KeyPair(publicKey, privateKey);

        } catch (Exception e) {
            Log.e(TAG, "Error retrieving key pair", e);
            return null;
        }
    }

    /**
     * Encrypt data using public key
     */
    public static String encryptData(String data, PublicKey publicKey) {
        try {
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            byte[] encryptedBytes = cipher.doFinal(data.getBytes());
            return Base64.encodeToString(encryptedBytes, Base64.DEFAULT);
        } catch (Exception e) {
            Log.e(TAG, "Error encrypting data", e);
            return null;
        }
    }

    /**
     * Decrypt data using private key
     */
    public static String decryptData(String encryptedData, PrivateKey privateKey) {
        try {
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            byte[] decryptedBytes = cipher.doFinal(Base64.decode(encryptedData, Base64.DEFAULT));
            return new String(decryptedBytes);
        } catch (Exception e) {
            Log.e(TAG, "Error decrypting data", e);
            return null;
        }
    }

    /**
     * Convert public key to Base64 string for sharing
     */
    public static String publicKeyToBase64(PublicKey publicKey) {
        return Base64.encodeToString(publicKey.getEncoded(), Base64.DEFAULT);
    }

    /**
     * Convert Base64 string to public key
     */
    public static PublicKey base64ToPublicKey(String base64Key) {
        try {
            byte[] keyBytes = Base64.decode(base64Key, Base64.DEFAULT);
            // In a real implementation, you would reconstruct the PublicKey object
            // This is a simplified version for demonstration
            return null;
        } catch (Exception e) {
            Log.e(TAG, "Error converting base64 to public key", e);
            return null;
        }
    }
}
