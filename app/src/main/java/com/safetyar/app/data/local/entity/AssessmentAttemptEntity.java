package com.safetyar.app.data.local.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "assessment_attempts")
public class AssessmentAttemptEntity {
    @PrimaryKey
    @NonNull
    private String attemptId;
    private String workerId;
    private String moduleId;
    private String scenarioId;
    private String sector;
    private int score;
    private int totalMarks;
    private boolean passed;
    private long startedAt;
    private long completedAt;
    private long durationSeconds;
    private boolean isSynced;

    public AssessmentAttemptEntity(@NonNull String attemptId, String workerId, String moduleId,
                                   String scenarioId, String sector, int score,
                                   int totalMarks, boolean passed, long startedAt,
                                   long completedAt, long durationSeconds, boolean isSynced) {
        this.attemptId = attemptId;
        this.workerId = workerId;
        this.moduleId = moduleId;
        this.scenarioId = scenarioId;
        this.sector = sector;
        this.score = score;
        this.totalMarks = totalMarks;
        this.passed = passed;
        this.startedAt = startedAt;
        this.completedAt = completedAt;
        this.durationSeconds = durationSeconds;
        this.isSynced = isSynced;
    }

    @NonNull
    public String getAttemptId() {
        return attemptId;
    }

    public void setAttemptId(@NonNull String attemptId) {
        this.attemptId = attemptId;
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

    public String getScenarioId() {
        return scenarioId;
    }

    public void setScenarioId(String scenarioId) {
        this.scenarioId = scenarioId;
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

    public int getTotalMarks() {
        return totalMarks;
    }

    public void setTotalMarks(int totalMarks) {
        this.totalMarks = totalMarks;
    }

    public boolean isPassed() {
        return passed;
    }

    public void setPassed(boolean passed) {
        this.passed = passed;
    }

    public long getStartedAt() {
        return startedAt;
    }

    public void setStartedAt(long startedAt) {
        this.startedAt = startedAt;
    }

    public long getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(long completedAt) {
        this.completedAt = completedAt;
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
