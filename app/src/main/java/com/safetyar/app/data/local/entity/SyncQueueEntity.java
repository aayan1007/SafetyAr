package com.safetyar.app.data.local.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "sync_queue")
public class SyncQueueEntity {
    @PrimaryKey(autoGenerate = true)
    private long id;
    @NonNull
    private String syncId;
    private String entityType; // "ASSESSMENT", "CERTIFICATE", "WORKER_PROFILE"
    private String entityId;
    private String payloadJson;
    private long createdAt;
    private int retryCount;
    private String status;     // "PENDING", "IN_PROGRESS", "FAILED"

    public SyncQueueEntity(@NonNull String syncId, String entityType, String entityId,
                           String payloadJson, long createdAt, int retryCount, String status) {
        this.syncId = syncId;
        this.entityType = entityType;
        this.entityId = entityId;
        this.payloadJson = payloadJson;
        this.createdAt = createdAt;
        this.retryCount = retryCount;
        this.status = status;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @NonNull
    public String getSyncId() {
        return syncId;
    }

    public void setSyncId(@NonNull String syncId) {
        this.syncId = syncId;
    }

    public String getEntityType() {
        return entityType;
    }

    public void setEntityType(String entityType) {
        this.entityType = entityType;
    }

    public String getEntityId() {
        return entityId;
    }

    public void setEntityId(String entityId) {
        this.entityId = entityId;
    }

    public String getPayloadJson() {
        return payloadJson;
    }

    public void setPayloadJson(String payloadJson) {
        this.payloadJson = payloadJson;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public int getRetryCount() {
        return retryCount;
    }

    public void setRetryCount(int retryCount) {
        this.retryCount = retryCount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
