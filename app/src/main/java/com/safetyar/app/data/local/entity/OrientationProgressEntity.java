package com.safetyar.app.data.local.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "orientation_progress")
public class OrientationProgressEntity {
    @PrimaryKey
    @NonNull
    private String orientationId;
    private String workerId;
    private long startDate;
    private int currentDay;
    private int daysCompleted;
    private int orientationPercentage;
    private boolean isCompleted;
    private long lastCheckInDate;

    public OrientationProgressEntity(@NonNull String orientationId, String workerId, long startDate,
                                     int currentDay, int daysCompleted, int orientationPercentage,
                                     boolean isCompleted, long lastCheckInDate) {
        this.orientationId = orientationId;
        this.workerId = workerId;
        this.startDate = startDate;
        this.currentDay = currentDay;
        this.daysCompleted = daysCompleted;
        this.orientationPercentage = orientationPercentage;
        this.isCompleted = isCompleted;
        this.lastCheckInDate = lastCheckInDate;
    }

    @NonNull
    public String getOrientationId() {
        return orientationId;
    }

    public void setOrientationId(@NonNull String orientationId) {
        this.orientationId = orientationId;
    }

    public String getWorkerId() {
        return workerId;
    }

    public void setWorkerId(String workerId) {
        this.workerId = workerId;
    }

    public long getStartDate() {
        return startDate;
    }

    public void setStartDate(long startDate) {
        this.startDate = startDate;
    }

    public int getCurrentDay() {
        return currentDay;
    }

    public void setCurrentDay(int currentDay) {
        this.currentDay = currentDay;
    }

    public int getDaysCompleted() {
        return daysCompleted;
    }

    public void setDaysCompleted(int daysCompleted) {
        this.daysCompleted = daysCompleted;
    }

    public int getOrientationPercentage() {
        return orientationPercentage;
    }

    public void setOrientationPercentage(int orientationPercentage) {
        this.orientationPercentage = orientationPercentage;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }

    public long getLastCheckInDate() {
        return lastCheckInDate;
    }

    public void setLastCheckInDate(long lastCheckInDate) {
        this.lastCheckInDate = lastCheckInDate;
    }
}
