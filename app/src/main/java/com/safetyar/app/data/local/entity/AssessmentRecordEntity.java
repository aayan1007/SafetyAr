package com.safetyar.app.data.local.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "assessment_records")
public class AssessmentRecordEntity {
    @PrimaryKey
    @NonNull
    private String assessmentId;
    private String workerId;
    private String moduleId;
    private String sector;
    private int score;
    private int totalHazards;
    private int hazardsIdentified;
    private int hazardsMitigated;
    private boolean passed;
    private long completionTimestamp;
    private long durationSeconds;
    private boolean isSynced;

    public AssessmentRecordEntity(@NonNull String assessmentId, String workerId, String moduleId,
                                  String sector, int score, int totalHazards,
                                  int hazardsIdentified, int hazardsMitigated,
                                  boolean passed, long completionTimestamp,
                                  long durationSeconds, boolean isSynced) {
        this.assessmentId = assessmentId;
        this.workerId = workerId;
        this.moduleId = moduleId;
        this.sector = sector;
        this.score = score;
        this.totalHazards = totalHazards;
        this.hazardsIdentified = hazardsIdentified;
        this.hazardsMitigated = hazardsMitigated;
        this.passed = passed;
        this.completionTimestamp = completionTimestamp;
        this.durationSeconds = durationSeconds;
        this.isSynced = isSynced;
    }

    @NonNull
    public String getAssessmentId() {
        return assessmentId;
    }

    public void setAssessmentId(@NonNull String assessmentId) {
        this.assessmentId = assessmentId;
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

    public String getSector() {
        return sector;
    }

    public void setSector(String sector) {
        this.sector = sector;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getTotalHazards() {
        return totalHazards;
    }

    public void setTotalHazards(int totalHazards) {
        this.totalHazards = totalHazards;
    }

    public int getHazardsIdentified() {
        return hazardsIdentified;
    }

    public void setHazardsIdentified(int hazardsIdentified) {
        this.hazardsIdentified = hazardsIdentified;
    }

    public int getHazardsMitigated() {
        return hazardsMitigated;
    }

    public void setHazardsMitigated(int hazardsMitigated) {
        this.hazardsMitigated = hazardsMitigated;
    }

    public boolean isPassed() {
        return passed;
    }

    public void setPassed(boolean passed) {
        this.passed = passed;
    }

    public long getCompletionTimestamp() {
        return completionTimestamp;
    }

    public void setCompletionTimestamp(long completionTimestamp) {
        this.completionTimestamp = completionTimestamp;
    }

    public long getDurationSeconds() {
        return durationSeconds;
    }

    public void setDurationSeconds(long durationSeconds) {
        this.durationSeconds = durationSeconds;
    }

    public boolean isSynced() {
        return isSynced;
    }

    public void setSynced(boolean synced) {
        isSynced = synced;
    }
}
