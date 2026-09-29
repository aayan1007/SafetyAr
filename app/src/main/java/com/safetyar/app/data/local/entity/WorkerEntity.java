package com.safetyar.app.data.local.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "workers")
public class WorkerEntity {
    @PrimaryKey
    @NonNull
    private String workerId;
    private String orgId;       // Foreign key referencing OrganizationEntity
    private String fullName;
    private String nationalId;
    private String trade;
    private String sector;      // COAL_MINING, STEEL_MANUFACTURING, MICA_PROCESSING
    private String enterprise;  // e.g. BCCL Dhanbad, Tata Steel, Koderma Mica Unit
    private String department;  // e.g. Underground Mine Section 4, Blast Furnace #2
    private String joiningDate; // e.g. "01 Sep 2026"
    private long orientationStartDate; // Timestamp when 30-day orientation commenced
    private String assignedModuleId;   // e.g. "MOD-COAL-01"
    private int completedModulesCount;
    private boolean isCertified;
    private long lastAssessmentTimestamp;
    private boolean isActiveSession;

    public WorkerEntity(@NonNull String workerId, String orgId, String fullName, String nationalId,
                        String trade, String sector, String enterprise,
                        String department, String joiningDate, long orientationStartDate,
                        String assignedModuleId, int completedModulesCount,
                        boolean isCertified, long lastAssessmentTimestamp,
                        boolean isActiveSession) {
        this.workerId = workerId;
        this.orgId = orgId;
        this.fullName = fullName;
        this.nationalId = nationalId;
        this.trade = trade;
        this.sector = sector;
        this.enterprise = enterprise;
        this.department = department;
        this.joiningDate = joiningDate;
        this.orientationStartDate = orientationStartDate;
        this.assignedModuleId = assignedModuleId;
        this.completedModulesCount = completedModulesCount;
        this.isCertified = isCertified;
        this.lastAssessmentTimestamp = lastAssessmentTimestamp;
        this.isActiveSession = isActiveSession;
    }

    @NonNull
    public String getWorkerId() {
        return workerId;
    }

    public void setWorkerId(@NonNull String workerId) {
        this.workerId = workerId;
    }

    public String getOrgId() {
        return orgId;
    }

    public void setOrgId(String orgId) {
        this.orgId = orgId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getNationalId() {
        return nationalId;
    }

    public void setNationalId(String nationalId) {
        this.nationalId = nationalId;
    }

    public String getTrade() {
        return trade;
    }

    public void setTrade(String trade) {
        this.trade = trade;
    }

    public String getSector() {
        return sector;
    }

    public void setSector(String sector) {
        this.sector = sector;
    }

    public String getEnterprise() {
        return enterprise;
    }

    public void setEnterprise(String enterprise) {
        this.enterprise = enterprise;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getJoiningDate() {
        return joiningDate;
    }

    public void setJoiningDate(String joiningDate) {
        this.joiningDate = joiningDate;
    }

    public long getOrientationStartDate() {
        return orientationStartDate;
    }

    public void setOrientationStartDate(long orientationStartDate) {
        this.orientationStartDate = orientationStartDate;
    }

    public String getAssignedModuleId() {
        return assignedModuleId;
    }

    public void setAssignedModuleId(String assignedModuleId) {
        this.assignedModuleId = assignedModuleId;
    }

    public int getCompletedModulesCount() {
        return completedModulesCount;
    }

    public void setCompletedModulesCount(int completedModulesCount) {
        this.completedModulesCount = completedModulesCount;
    }

    public boolean isCertified() {
        return isCertified;
    }

    public void setCertified(boolean certified) {
        this.isCertified = certified;
    }

    public long getLastAssessmentTimestamp() {
        return lastAssessmentTimestamp;
    }

    public void setLastAssessmentTimestamp(long lastAssessmentTimestamp) {
        this.lastAssessmentTimestamp = lastAssessmentTimestamp;
    }

    public boolean isActiveSession() {
        return isActiveSession;
    }

    public void setActiveSession(boolean activeSession) {
        this.isActiveSession = activeSession;
    }

    public int calculateOrientationDay() {
        if (orientationStartDate <= 0) return 1;
        long elapsedMs = System.currentTimeMillis() - orientationStartDate;
        long elapsedDays = elapsedMs / (1000L * 60 * 60 * 24);
        int day = (int) elapsedDays + 1;
        return Math.max(1, Math.min(30, day));
    }

    public int calculateOrientationPercentage() {
        int day = calculateOrientationDay();
        return (day * 100) / 30;
    }
}
