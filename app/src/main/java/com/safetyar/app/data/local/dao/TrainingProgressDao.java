package com.safetyar.app.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.safetyar.app.data.local.entity.TrainingProgressEntity;

import java.util.List;

@Dao
public interface TrainingProgressDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertProgress(TrainingProgressEntity progress);

    @Update
    void updateProgress(TrainingProgressEntity progress);

    @Query("SELECT * FROM training_progress WHERE workerId = :workerId")
    LiveData<List<TrainingProgressEntity>> getProgressForWorker(String workerId);

    @Query("SELECT * FROM training_progress WHERE workerId = :workerId AND moduleId = :moduleId LIMIT 1")
    LiveData<TrainingProgressEntity> getModuleProgress(String workerId, String moduleId);

    @Query("SELECT * FROM training_progress WHERE workerId = :workerId AND moduleId = :moduleId LIMIT 1")
    TrainingProgressEntity getModuleProgressSync(String workerId, String moduleId);
}
