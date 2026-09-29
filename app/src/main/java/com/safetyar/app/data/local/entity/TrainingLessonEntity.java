package com.safetyar.app.data.local.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "training_lessons")
public class TrainingLessonEntity {
    @PrimaryKey
    @NonNull
    private String lessonId;
    private String moduleId; // References TrainingModuleEntity
    private int lessonNumber;
    private String title;
    private String contentBody;
    private String keyTakeaways;
    private int durationMinutes;
    private boolean isCompleted;

    public TrainingLessonEntity(@NonNull String lessonId, String moduleId, int lessonNumber,
                                String title, String contentBody, String keyTakeaways,
                                int durationMinutes, boolean isCompleted) {
        this.lessonId = lessonId;
        this.moduleId = moduleId;
        this.lessonNumber = lessonNumber;
        this.title = title;
        this.contentBody = contentBody;
        this.keyTakeaways = keyTakeaways;
        this.durationMinutes = durationMinutes;
        this.isCompleted = isCompleted;
    }

    @NonNull
    public String getLessonId() {
        return lessonId;
    }

    public void setLessonId(@NonNull String lessonId) {
        this.lessonId = lessonId;
    }

    public String getModuleId() {
        return moduleId;
    }

    public void setModuleId(String moduleId) {
        this.moduleId = moduleId;
    }

    public int getLessonNumber() {
        return lessonNumber;
    }

    public void setLessonNumber(int lessonNumber) {
        this.lessonNumber = lessonNumber;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContentBody() {
        return contentBody;
    }

    public void setContentBody(String contentBody) {
        this.contentBody = contentBody;
    }

    public String getKeyTakeaways() {
        return keyTakeaways;
    }

    public void setKeyTakeaways(String keyTakeaways) {
        this.keyTakeaways = keyTakeaways;
    }

    public int getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(int durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }
}
