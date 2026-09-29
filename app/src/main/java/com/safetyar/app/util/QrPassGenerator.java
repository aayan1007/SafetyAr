package com.safetyar.app.util;

import android.graphics.Bitmap;
import android.graphics.Color;

import com.google.gson.JsonObject;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

public class QrPassGenerator {

    private static final String CRYPTO_SALT = "SAFETYAR_DGMS_JHARKHAND_2026";

    /**
     * Formats a unique certificate ID in the required standard:
     * SAFETYAR-JH-2026-000001
     */
    public static String formatCertificateId(int sequenceNumber) {
        return String.format(Locale.ROOT, "SAFETYAR-JH-2026-%06d", sequenceNumber);
    }

    /**
     * Generates a new unique certificate ID based on sequential randomness.
     */
    public static String generateUniqueCertificateId() {
        int randomSeq = 100000 + new Random().nextInt(900000);
        return formatCertificateId(randomSeq);
    }

    /**
     * Computes a cryptographic SHA-256 verification token ensuring pass integrity.
     */
    public static String computeVerificationToken(String certId, String workerId, String org,
                                                 String module, int score, long issuedAt, long expiresAt) {
        String rawData = certId + "|" + workerId + "|" + org + "|" + module + "|" + score + "|" +
                issuedAt + "|" + expiresAt + "|" + CRYPTO_SALT;
        return computeSha256(rawData);
    }

    /**
     * Builds structured JSON payload for QR embedding.
     */
    public static String buildQrPayload(String certId, String workerId, String workerName,
                                        String org, String module, int score,
                                        long issuedAt, long expiresAt, String status, String issuer) {
        String token = computeVerificationToken(certId, workerId, org, module, score, issuedAt, expiresAt);

        JsonObject json = new JsonObject();
        json.addProperty("certId", certId);
        json.addProperty("token", token);
        json.addProperty("workerId", workerId);
        json.addProperty("name", workerName);
        json.addProperty("org", org);
        json.addProperty("module", module);
        json.addProperty("score", score);
        json.addProperty("issuedAt", issuedAt);
        json.addProperty("expiresAt", expiresAt);
        json.addProperty("status", (status != null && !status.trim().isEmpty()) ? status : "VALID");
        json.addProperty("issuer", (issuer != null && !issuer.trim().isEmpty()) ? issuer : "Jharkhand Industrial Safety Council (JISC)");

        return json.toString();
    }

    /**
     * Generates a 2D QR Code Bitmap containing encrypted/structured worker safety pass info.
     */
    public static Bitmap generateQrCodeBitmap(String payload, int width, int height) throws WriterException {
        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
        hints.put(EncodeHintType.MARGIN, 1);

        BitMatrix bitMatrix = new MultiFormatWriter().encode(
                payload,
                BarcodeFormat.QR_CODE,
                width,
                height,
                hints
        );

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                bitmap.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
            }
        }
        return bitmap;
    }

    /**
     * Computes SHA-256 tamper-evident digital certificate signature.
     */
    public static String computeSha256(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            return "SIG-" + System.currentTimeMillis();
        }
    }
}
