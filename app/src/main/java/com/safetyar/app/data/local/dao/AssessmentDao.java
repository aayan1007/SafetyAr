package com.safetyar.app.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.safetyar.app.data.local.entity.AssessmentRecordEntity;

import java.util.List;

@Dao
public interface AssessmentDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAssessment(AssessmentRecordEntity record);

    @Update
    void updateAssessment(AssessmentRecordEntity record);

    @Query("SELECT * FROM assessment_records ORDER BY completionTimestamp DESC")
    LiveData<List<AssessmentRecordEntity>> getAllAssessments();

    @Query("SELECT * FROM assessment_records WHERE workerId = :workerId ORDER BY completionTimestamp DESC")
    LiveData<List<AssessmentRecordEntity>> getAssessmentsByWorker(String workerId);

    @Query("SELECT * FROM assessment_records WHERE assessmentId = :assessmentId LIMIT 1")
    LiveData<AssessmentRecordEntity> getAssessmentById(String assessmentId);

    @Query("SELECT * FROM assessment_records WHERE isSynced = 0")
    List<AssessmentRecordEntity> getUnsyncedAssessments();

    @Query("UPDATE assessment_records SET isSynced = 1 WHERE assessmentId = :assessmentId")
    void markAsSynced(String assessmentId);
}
