package com.safetyar.app.data.service;

public interface CertificateVerificationService {

    enum VerificationStatus {
        VALID,
        EXPIRED,
        REVOKED,
        NOT_FOUND
    }

    class VerificationResult {
        private final VerificationStatus status;
        private final String certificateId;
        private final String workerId;
        private final String workerName;
        private final String organization;
        private final String trainingModule;
        private final int score;
        private final long issuedTimestamp;
        private final long expiresTimestamp;
        private final String issuingAuthority;
        private final String verificationToken;
        private final String message;
        private final String complianceNote;

        public VerificationResult(VerificationStatus status, String certificateId, String workerId,
                                  String workerName, String organization, String trainingModule,
                                  int score, long issuedTimestamp, long expiresTimestamp,
                                  String issuingAuthority, String verificationToken,
                                  String message, String complianceNote) {
            this.status = status;
            this.certificateId = certificateId;
            this.workerId = workerId;
            this.workerName = workerName;
            this.organization = organization;
            this.trainingModule = trainingModule;
            this.score = score;
            this.issuedTimestamp = issuedTimestamp;
            this.expiresTimestamp = expiresTimestamp;
            this.issuingAuthority = issuingAuthority;
            this.verificationToken = verificationToken;
            this.message = message;
            this.complianceNote = complianceNote;
        }

        public VerificationStatus getStatus() {
            return status;
        }

        public String getCertificateId() {
            return certificateId;
        }

        public String getWorkerId() {
            return workerId;
        }

        public String getWorkerName() {
            return workerName;
        }

        public String getOrganization() {
            return organization;
        }

        public String getTrainingModule() {
            return trainingModule;
        }

        public int getScore() {
            return score;
        }

        public long getIssuedTimestamp() {
            return issuedTimestamp;
        }

        public long getExpiresTimestamp() {
            return expiresTimestamp;
        }

        public String getIssuingAuthority() {
            return issuingAuthority;
        }

        public String getVerificationToken() {
            return verificationToken;
        }

        public String getMessage() {
            return message;
        }

        public String getComplianceNote() {
            return complianceNote;
        }
    }

    interface VerificationCallback {
        void onResult(VerificationResult result);
    }

    void verify(String rawPayloadOrCertId, VerificationCallback callback);
}
