package com.safetyar.app.domain;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.safetyar.app.util.QrPassGenerator;

import org.junit.Test;

import static org.junit.Assert.*;

public class QrPassVerificationTest {

    @Test
    public void testCertificateIdFormatCompliance() {
        String certId = QrPassGenerator.formatCertificateId(1);
        assertEquals("SAFETYAR-JH-2026-000001", certId);

        String randomCertId = QrPassGenerator.generateUniqueCertificateId();
        assertNotNull(randomCertId);
        assertTrue("Must match SAFETYAR-JH-2026-XXXXXX format",
                randomCertId.matches("^SAFETYAR-JH-2026-\\d{6}$"));
    }

    @Test
    public void testCryptographicSha256TokenIntegrityAndTamperResistance() {
        long issuedAt = 1773400000000L;
        long expiresAt = issuedAt + (365L * 24 * 60 * 60 * 1000L);

        String token1 = QrPassGenerator.computeVerificationToken(
                "SAFETYAR-JH-2026-000001", "WRK-JH-COAL-0891", "BCCL",
                "Fire & Explosion Emergency Response", 94, issuedAt, expiresAt);

        String token2 = QrPassGenerator.computeVerificationToken(
                "SAFETYAR-JH-2026-000001", "WRK-JH-COAL-0891", "BCCL",
                "Fire & Explosion Emergency Response", 94, issuedAt, expiresAt);

        assertNotNull(token1);
        assertEquals(64, token1.length()); // SHA-256 hex string length
        assertEquals("Deterministic hashing must produce identical token for identical input", token1, token2);

        // Tamper test: score altered from 94 to 95
        String tamperedToken = QrPassGenerator.computeVerificationToken(
                "SAFETYAR-JH-2026-000001", "WRK-JH-COAL-0891", "BCCL",
                "Fire & Explosion Emergency Response", 95, issuedAt, expiresAt);

        assertNotEquals("Tampered score must produce completely different hash token", token1, tamperedToken);
    }

    @Test
    public void testQrPayloadStructure() {
        long issuedAt = 1773400000000L;
        long expiresAt = issuedAt + (365L * 24 * 60 * 60 * 1000L);

        String payload = QrPassGenerator.buildQrPayload(
                "SAFETYAR-JH-2026-000001", "WRK-JH-COAL-0891", "Ramesh Soren",
                "Bharat Coking Coal Limited (BCCL)", "Fire & Explosion Emergency Response",
                94, issuedAt, expiresAt, "VALID", "Jharkhand Industrial Safety Council (JISC)");

        assertNotNull(payload);
        JsonObject json = JsonParser.parseString(payload).getAsJsonObject();

        assertEquals("SAFETYAR-JH-2026-000001", json.get("certId").getAsString());
        assertEquals("WRK-JH-COAL-0891", json.get("workerId").getAsString());
        assertEquals("Ramesh Soren", json.get("name").getAsString());
        assertEquals(94, json.get("score").getAsInt());
        assertEquals("VALID", json.get("status").getAsString());
        assertNotNull(json.get("token").getAsString());
        assertEquals(64, json.get("token").getAsString().length());
    }

    @Test
    public void testFourVerificationStatesLogic() {
        long now = System.currentTimeMillis();
        long oneYear = 365L * 24 * 60 * 60 * 1000L;

        // State 1: VALID
        long issuedValid = now - (10L * 24 * 60 * 60 * 1000L);
        long expiresValid = issuedValid + oneYear;
        String statusValid = evaluateVerificationState("VALID", expiresValid, now);
        assertEquals("VALID", statusValid);

        // State 2: EXPIRED
        long issuedExpired = now - (400L * 24 * 60 * 60 * 1000L);
        long expiresExpired = now - (35L * 24 * 60 * 60 * 1000L); // 35 days ago
        String statusExpired = evaluateVerificationState("VALID", expiresExpired, now);
        assertEquals("EXPIRED", statusExpired);

        // State 3: REVOKED
        String statusRevoked = evaluateVerificationState("REVOKED", expiresValid, now);
        assertEquals("REVOKED", statusRevoked);

        // State 4: NOT FOUND
        String statusNotFound = evaluateVerificationState(null, 0L, now);
        assertEquals("NOT_FOUND", statusNotFound);
    }

    private String evaluateVerificationState(String dbStatus, long expiresTimestamp, long currentTimestamp) {
        if (dbStatus == null) {
            return "NOT_FOUND";
        }
        if ("REVOKED".equalsIgnoreCase(dbStatus)) {
            return "REVOKED";
        }
        if (currentTimestamp > expiresTimestamp) {
            return "EXPIRED";
        }
        return "VALID";
    }
}
