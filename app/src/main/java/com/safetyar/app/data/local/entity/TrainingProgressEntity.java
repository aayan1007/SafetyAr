package com.safetyar.app.data.local.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "training_progress")
public class TrainingProgressEntity {
    @PrimaryKey
    @NonNull
    private String progressId;
    private String workerId;
    private String moduleId;
    private String status; // "NOT_STARTED", "IN_PROGRESS", "COMPLETED"
    private int percentComplete;
    private long lastAccessedAt;

    public TrainingProgressEntity(@NonNull String progressId, String workerId, String moduleId,
                                  String status, int percentComplete, long lastAccessedAt) {
        this.progressId = progressId;
        this.workerId = workerId;
        this.moduleId = moduleId;
        this.status = status;
        this.percentComplete = percentComplete;
        this.lastAccessedAt = lastAccessedAt;
    }

    @NonNull
    public String getProgressId() {
        return progressId;
    }

    public void setProgressId(@NonNull String progressId) {
        this.progressId = progressId;
    }

    public String getWorkerId() {
        return workerId;
    }

    public void setWorkerId(String workerId) {
        this.workerId = workerId;
    }

    public String getModuleId() {
        return moduleId;
    }

    public void setModuleId(String moduleId) {
        this.moduleId = moduleId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getPercentComplete() {
        return percentComplete;
    }

    public void setPercentComplete(int percentComplete) {
        this.percentComplete = percentComplete;
    }

    public long getLastAccessedAt() {
        return lastAccessedAt;
    }

    public void setLastAccessedAt(long lastAccessedAt) {
        this.lastAccessedAt = lastAccessedAt;
    }
}
