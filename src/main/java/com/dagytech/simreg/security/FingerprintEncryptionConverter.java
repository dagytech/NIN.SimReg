package com.dagytech.simreg.security;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * Hii "INAFICHA" (encrypt) data nyeti - fingerprint template - KABLA
 * haijaandikwa kwenye database, na "INAFUNGUA" (decrypt) inaposomwa.
 *
 * Kwenye database, badala ya kuona "FP-TEMPLATE-0001" moja kwa moja, utaona
 * maandishi yasiyoeleweka (ciphertext) - hata mtu akipata database moja kwa
 * moja (mfano wizi wa database), hataweza kusoma fingerprint templates halisi
 * bila kuwa na "encryption-key".
 *
 * Tunatumia AES-256-GCM (AES = algorithm, GCM = "mode" inayotoa uthibitisho
 * wa uadilifu - yaani inagundua kama mtu amejaribu "kubabua" data iliyofichwa).
 */
@Component
@Converter
public class FingerprintEncryptionConverter implements AttributeConverter<String, String> {

    private static final String ALGORITHM = "AES/GCM/NoPadding";
    private static final int GCM_IV_LENGTH = 12;   // bytes
    private static final int GCM_TAG_LENGTH = 128; // bits

    private static String staticKey;

    @Value("${app.security.encryption-key}")
    public void setKey(String key) {
        staticKey = key;
    }

    @Override
    public String convertToDatabaseColumn(String plainText) {
        if (plainText == null) return null;
        try {
            SecretKeySpec keySpec = new SecretKeySpec(staticKey.getBytes(StandardCharsets.UTF_8), "AES");
            byte[] iv = new byte[GCM_IV_LENGTH];
            new SecureRandom().nextBytes(iv);

            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, new GCMParameterSpec(GCM_TAG_LENGTH, iv));
            byte[] cipherText = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));

            // Tunahifadhi IV pamoja na ciphertext (IV siyo siri - inahitajika tu kufungua)
            byte[] combined = new byte[iv.length + cipherText.length];
            System.arraycopy(iv, 0, combined, 0, iv.length);
            System.arraycopy(cipherText, 0, combined, iv.length, cipherText.length);

            return Base64.getEncoder().encodeToString(combined);
        } catch (Exception e) {
            throw new RuntimeException("Imeshindwa kuficha (encrypt) data: " + e.getMessage(), e);
        }
    }

    @Override
    public String convertToEntityAttribute(String encrypted) {
        if (encrypted == null) return null;
        try {
            byte[] combined = Base64.getDecoder().decode(encrypted);
            byte[] iv = new byte[GCM_IV_LENGTH];
            byte[] cipherText = new byte[combined.length - GCM_IV_LENGTH];
            System.arraycopy(combined, 0, iv, 0, GCM_IV_LENGTH);
            System.arraycopy(combined, GCM_IV_LENGTH, cipherText, 0, cipherText.length);

            SecretKeySpec keySpec = new SecretKeySpec(staticKey.getBytes(StandardCharsets.UTF_8), "AES");
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, keySpec, new GCMParameterSpec(GCM_TAG_LENGTH, iv));
            byte[] plainBytes = cipher.doFinal(cipherText);

            return new String(plainBytes, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException("Imeshindwa kufungua (decrypt) data: " + e.getMessage(), e);
        }
    }
}
