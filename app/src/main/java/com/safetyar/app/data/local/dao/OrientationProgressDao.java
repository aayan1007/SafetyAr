package com.safetyar.app.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.safetyar.app.data.local.entity.OrientationProgressEntity;

@Dao
public interface OrientationProgressDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertOrientation(OrientationProgressEntity orientation);

    @Update
    void updateOrientation(OrientationProgressEntity orientation);

    @Query("SELECT * FROM orientation_progress WHERE workerId = :workerId LIMIT 1")
    LiveData<OrientationProgressEntity> getOrientationForWorker(String workerId);

    @Query("SELECT * FROM orientation_progress WHERE workerId = :workerId LIMIT 1")
    OrientationProgressEntity getOrientationForWorkerSync(String workerId);
}
