package com.safetyar.app.data.local.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "training_modules")
public class TrainingModuleEntity {
    @PrimaryKey
    @NonNull
    private String moduleId;
    private String sector;      // COAL_MINING, STEEL_MANUFACTURING, MICA_PROCESSING
    private String title;
    private String subtitle;
    private String dgmsRegulationCode; // e.g. "DGMS Tech Circular 3 of 2019"
    private String sopGuidelines;      // Bullet points of SOP
    private int hazardCount;
    private int passingScore;
    private int estimatedMinutes;
    private int totalLessons;
    private boolean isCompleted;

    public TrainingModuleEntity(@NonNull String moduleId, String sector, String title,
                                String subtitle, String dgmsRegulationCode, String sopGuidelines,
                                int hazardCount, int passingScore, int estimatedMinutes,
                                int totalLessons, boolean isCompleted) {
        this.moduleId = moduleId;
        this.sector = sector;
        this.title = title;
        this.subtitle = subtitle;
        this.dgmsRegulationCode = dgmsRegulationCode;
        this.sopGuidelines = sopGuidelines;
        this.hazardCount = hazardCount;
        this.passingScore = passingScore;
        this.estimatedMinutes = estimatedMinutes;
        this.totalLessons = totalLessons;
        this.isCompleted = isCompleted;
    }

    @NonNull
    public String getModuleId() {
        return moduleId;
    }

    public void setModuleId(@NonNull String moduleId) {
        this.moduleId = moduleId;
    }

    public String getSector() {
        return sector;
    }

    public void setSector(String sector) {
        this.sector = sector;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public String getDgmsRegulationCode() {
        return dgmsRegulationCode;
    }

    public void setDgmsRegulationCode(String dgmsRegulationCode) {
        this.dgmsRegulationCode = dgmsRegulationCode;
    }

    public String getSopGuidelines() {
        return sopGuidelines;
    }

    public void setSopGuidelines(String sopGuidelines) {
        this.sopGuidelines = sopGuidelines;
    }

    public int getHazardCount() {
        return hazardCount;
    }

    public void setHazardCount(int hazardCount) {
        this.hazardCount = hazardCount;
    }

    public int getPassingScore() {
        return passingScore;
    }

    public void setPassingScore(int passingScore) {
        this.passingScore = passingScore;
    }

    public int getEstimatedMinutes() {
        return estimatedMinutes;
    }

    public void setEstimatedMinutes(int estimatedMinutes) {
        this.estimatedMinutes = estimatedMinutes;
    }

    public int getTotalLessons() {
        return totalLessons;
    }

    public void setTotalLessons(int totalLessons) {
        this.totalLessons = totalLessons;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }
}
