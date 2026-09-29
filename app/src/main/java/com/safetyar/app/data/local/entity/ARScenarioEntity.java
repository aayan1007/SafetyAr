package com.safetyar.app.data.local.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "ar_scenarios")
public class ARScenarioEntity {
    @PrimaryKey
    @NonNull
    private String scenarioId;
    private String moduleId;
    private String sector;
    private String scenarioTitle;
    private String environmentType; // "UNDERGROUND_COAL_SEAM", "BLAST_FURNACE_FLOOR", "MICA_SORTING_SHED"
    private int hazardCount;
    private int passingScore;
    private int timeLimitSeconds;
    private String hazardsConfigJson; // Serialized list of initial hazard locations

    public ARScenarioEntity(@NonNull String scenarioId, String moduleId, String sector,
                            String scenarioTitle, String environmentType, int hazardCount,
                            int passingScore, int timeLimitSeconds, String hazardsConfigJson) {
        this.scenarioId = scenarioId;
        this.moduleId = moduleId;
        this.sector = sector;
        this.scenarioTitle = scenarioTitle;
        this.environmentType = environmentType;
        this.hazardCount = hazardCount;
        this.passingScore = passingScore;
        this.timeLimitSeconds = timeLimitSeconds;
        this.hazardsConfigJson = hazardsConfigJson;
    }

    @NonNull
    public String getScenarioId() {
        return scenarioId;
    }

    public void setScenarioId(@NonNull String scenarioId) {
        this.scenarioId = scenarioId;
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

    public String getScenarioTitle() {
        return scenarioTitle;
    }

    public void setScenarioTitle(String scenarioTitle) {
        this.scenarioTitle = scenarioTitle;
    }

    public String getEnvironmentType() {
        return environmentType;
    }

    public void setEnvironmentType(String environmentType) {
        this.environmentType = environmentType;
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

    public int getTimeLimitSeconds() {
        return timeLimitSeconds;
    }

    public void setTimeLimitSeconds(int timeLimitSeconds) {
        this.timeLimitSeconds = timeLimitSeconds;
    }

    public String getHazardsConfigJson() {
        return hazardsConfigJson;
    }

    public void setHazardsConfigJson(String hazardsConfigJson) {
        this.hazardsConfigJson = hazardsConfigJson;
    }
}
