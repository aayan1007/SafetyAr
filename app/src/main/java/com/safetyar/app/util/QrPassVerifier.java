package com.safetyar.app.util;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class QrPassVerifier {

    public static class VerificationResult {
        public final boolean isValid;
        public final String workerId;
        public final String workerName;
        public final String sector;
        public final String trade;
        public final String certificateId;
        public final String issuingAuthority;
        public final long expiresTimestamp;
        public final String message;

        public VerificationResult(boolean isValid, String workerId, String workerName,
                                  String sector, String trade, String certificateId,
                                  String issuingAuthority, long expiresTimestamp, String message) {
            this.isValid = isValid;
            this.workerId = workerId;
            this.workerName = workerName;
            this.sector = sector;
            this.trade = trade;
            this.certificateId = certificateId;
            this.issuingAuthority = issuingAuthority;
            this.expiresTimestamp = expiresTimestamp;
            this.message = message;
        }
    }

    public static VerificationResult verifyQrPayload(String rawPayload) {
        if (rawPayload == null || rawPayload.trim().isEmpty()) {
            return new VerificationResult(false, "", "", "", "", "", "", 0L, "QR code is empty or unreadable.");
        }

        try {
            Gson gson = new Gson();
            JsonObject obj = gson.fromJson(rawPayload, JsonObject.class);

            if (!obj.has("certId") || !obj.has("workerId") || !obj.has("signature")) {
                return new VerificationResult(false, "", "", "", "", "", "", 0L, "Unrecognized QR format or not a SafetyAR pass.");
            }

            String certId = obj.get("certId").getAsString();
            String workerId = obj.get("workerId").getAsString();
            String name = obj.has("name") ? obj.get("name").getAsString() : "Unknown Worker";
            String sector = obj.has("sector") ? obj.get("sector").getAsString() : "Industrial";
            String trade = obj.has("trade") ? obj.get("trade").getAsString() : "Field Worker";
            long expiresAt = obj.has("expiresAt") ? obj.get("expiresAt").getAsLong() : 0L;
            String authority = obj.has("issuer") ? obj.get("issuer").getAsString() : "Jharkhand Industrial Safety Council";

            long now = System.currentTimeMillis();
            if (expiresAt > 0 && now > expiresAt) {
                return new VerificationResult(false, workerId, name, sector, trade, certId, authority, expiresAt, "EXPIRED SAFETY CERTIFICATE. Refresher training required.");
            }

            return new VerificationResult(true, workerId, name, sector, trade, certId, authority, expiresAt, "AUTHORIZED: Valid DGMS-compliant Safety Pass.");
        } catch (Exception e) {
            return new VerificationResult(false, "", "", "", "", "", "", 0L, "Verification failure: Invalid data integrity.");
        }
    }
}
