package com.safetyar.app.data.remote.dto;

import com.safetyar.app.data.local.entity.AssessmentRecordEntity;
import com.safetyar.app.data.local.entity.CertificateEntity;

import java.util.List;

public class SyncPayloadDto {
    private String workerId;
    private String deviceId;
    private long timestamp;
    private List<AssessmentRecordEntity> assessments;
    private List<CertificateEntity> certificates;

    public SyncPayloadDto(String workerId, String deviceId, long timestamp,
                          List<AssessmentRecordEntity> assessments,
                          List<CertificateEntity> certificates) {
        this.workerId = workerId;
        this.deviceId = deviceId;
        this.timestamp = timestamp;
        this.assessments = assessments;
        this.certificates = certificates;
    }

    public String getWorkerId() {
        return workerId;
    }

    public void setWorkerId(String workerId) {
        this.workerId = workerId;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public List<AssessmentRecordEntity> getAssessments() {
        return assessments;
    }

    public void setAssessments(List<AssessmentRecordEntity> assessments) {
        this.assessments = assessments;
    }

    public List<CertificateEntity> getCertificates() {
        return certificates;
    }

    public void setCertificates(List<CertificateEntity> certificates) {
        this.certificates = certificates;
    }
}
