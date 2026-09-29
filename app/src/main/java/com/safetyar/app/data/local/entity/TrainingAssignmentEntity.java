package com.safetyar.app.data.local.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "training_assignments")
public class TrainingAssignmentEntity {
    @PrimaryKey
    @NonNull
    private String assignmentId;
    private String workerId;
    private String moduleId;
    private String assignedBySupervisor; // e.g. "Overman B. N. Singh"
    private long assignedDate;
    private long dueDate;
    private String status; // "ASSIGNED", "IN_PROGRESS", "SUBMITTED", "OVERDUE"
    private String priority; // "HIGH", "MEDIUM", "LOW"

    public TrainingAssignmentEntity(@NonNull String assignmentId, String workerId, String moduleId,
                                    String assignedBySupervisor, long assignedDate, long dueDate,
                                    String status, String priority) {
        this.assignmentId = assignmentId;
        this.workerId = workerId;
        this.moduleId = moduleId;
        this.assignedBySupervisor = assignedBySupervisor;
        this.assignedDate = assignedDate;
        this.dueDate = dueDate;
        this.status = status;
        this.priority = priority;
    }

    @NonNull
    public String getAssignmentId() {
        return assignmentId;
    }

    public void setAssignmentId(@NonNull String assignmentId) {
        this.assignmentId = assignmentId;
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

    public String getAssignedBySupervisor() {
        return assignedBySupervisor;
    }

    public void setAssignedBySupervisor(String assignedBySupervisor) {
        this.assignedBySupervisor = assignedBySupervisor;
    }

    public long getAssignedDate() {
        return assignedDate;
    }

    public void setAssignedDate(long assignedDate) {
        this.assignedDate = assignedDate;
    }

    public long getDueDate() {
        return dueDate;
    }

    public void setDueDate(long dueDate) {
        this.dueDate = dueDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }
}
