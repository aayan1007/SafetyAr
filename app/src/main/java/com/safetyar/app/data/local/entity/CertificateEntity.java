package com.safetyar.app.data.local.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "certificates")
public class CertificateEntity {
    @PrimaryKey
    @NonNull
    private String certificateId;
    private String workerId;
    private String workerName;
    private String organization;
    private String trainingModule;
    private int score;
    private String sector;
    private String trade;
    private String certificateHash; // SHA-256 tamper-evident hash
    private String verificationToken; // Cryptographic verification token
    private String qrCodePayload;   // JSON payload stored in QR code
    private long issuedTimestamp;
    private long expiresTimestamp;
    private String issuingAuthority; // e.g. "Jharkhand Industrial Safety Council (JISC)"
    private String status;          // "VALID", "EXPIRED", "REVOKED"
    private boolean isSynced;

    public CertificateEntity(@NonNull String certificateId, String workerId, String workerName,
                             String organization, String trainingModule, int score,
                             String sector, String trade, String certificateHash,
                             String verificationToken, String qrCodePayload,
                             long issuedTimestamp, long expiresTimestamp,
                             String issuingAuthority, String status, boolean isSynced) {
        this.certificateId = certificateId;
        this.workerId = workerId;
        this.workerName = workerName;
        this.organization = (organization != null && !organization.trim().isEmpty()) ? organization : "Jharkhand Industrial Safety Council (JISC)";
        this.trainingModule = (trainingModule != null && !trainingModule.trim().isEmpty()) ? trainingModule : "Industrial Safety Fundamentals";
        this.score = score;
        this.sector = sector;
        this.trade = trade;
        this.certificateHash = certificateHash;
        this.verificationToken = (verificationToken != null && !verificationToken.trim().isEmpty()) ? verificationToken : certificateHash;
        this.qrCodePayload = qrCodePayload;
        this.issuedTimestamp = issuedTimestamp;
        this.expiresTimestamp = expiresTimestamp;
        this.issuingAuthority = issuingAuthority;
        this.status = (status != null && !status.trim().isEmpty()) ? status : "VALID";
        this.isSynced = isSynced;
    }

    @Ignore
    public CertificateEntity(@NonNull String certificateId, String workerId, String workerName,
                             String sector, String trade, String certificateHash,
                             String qrCodePayload, long issuedTimestamp, long expiresTimestamp,
                             String issuingAuthority, boolean isSynced) {
        this(certificateId, workerId, workerName,
             "Jharkhand Mining & Industrial Enterprise",
             sector, 85,
             sector, trade, certificateHash,
             certificateHash, qrCodePayload,
             issuedTimestamp, expiresTimestamp,
             issuingAuthority, "VALID", isSynced);
    }

    @NonNull
    public String getCertificateId() {
        return certificateId;
    }

    public void setCertificateId(@NonNull String certificateId) {
        this.certificateId = certificateId;
    }

    public String getWorkerId() {
        return workerId;
    }

    public void setWorkerId(String workerId) {
        this.workerId = workerId;
    }

    public String getWorkerName() {
        return workerName;
    }

    public void setWorkerName(String workerName) {
        this.workerName = workerName;
    }

    public String getOrganization() {
        return organization;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
    }

    public String getTrainingModule() {
        return trainingModule;
    }

    public void setTrainingModule(String trainingModule) {
        this.trainingModule = trainingModule;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getSector() {
        return sector;
    }

    public void setSector(String sector) {
        this.sector = sector;
    }

    public String getTrade() {
        return trade;
    }

    public void setTrade(String trade) {
        this.trade = trade;
    }

    public String getCertificateHash() {
        return certificateHash;
    }

    public void setCertificateHash(String certificateHash) {
        this.certificateHash = certificateHash;
    }

    public String getVerificationToken() {
        return verificationToken;
    }

    public void setVerificationToken(String verificationToken) {
        this.verificationToken = verificationToken;
    }

    public String getQrCodePayload() {
        return qrCodePayload;
    }

    public void setQrCodePayload(String qrCodePayload) {
        this.qrCodePayload = qrCodePayload;
    }

    public long getIssuedTimestamp() {
        return issuedTimestamp;
    }

    public void setIssuedTimestamp(long issuedTimestamp) {
        this.issuedTimestamp = issuedTimestamp;
    }

    public long getExpiresTimestamp() {
        return expiresTimestamp;
    }

    public void setExpiresTimestamp(long expiresTimestamp) {
        this.expiresTimestamp = expiresTimestamp;
    }

    public String getIssuingAuthority() {
        return issuingAuthority;
    }

    public void setIssuingAuthority(String issuingAuthority) {
        this.issuingAuthority = issuingAuthority;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isSynced() {
        return isSynced;
    }

    public void setSynced(boolean synced) {
        isSynced = synced;
    }
}
