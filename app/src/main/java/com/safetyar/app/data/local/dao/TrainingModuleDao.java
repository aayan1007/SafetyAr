package com.safetyar.app.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.safetyar.app.data.local.entity.TrainingModuleEntity;

import java.util.List;

@Dao
public interface TrainingModuleDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertModules(List<TrainingModuleEntity> modules);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertModule(TrainingModuleEntity module);

    @Update
    void updateModule(TrainingModuleEntity module);

    @Query("SELECT * FROM training_modules ORDER BY moduleId ASC")
    LiveData<List<TrainingModuleEntity>> getAllModules();

    @Query("SELECT * FROM training_modules WHERE sector = :sector ORDER BY moduleId ASC")
    LiveData<List<TrainingModuleEntity>> getModulesBySector(String sector);

    @Query("SELECT * FROM training_modules WHERE moduleId = :moduleId LIMIT 1")
    LiveData<TrainingModuleEntity> getModuleById(String moduleId);

    @Query("SELECT * FROM training_modules WHERE moduleId = :moduleId LIMIT 1")
    TrainingModuleEntity getModuleByIdSync(String moduleId);

    @Query("SELECT COUNT(*) FROM training_modules WHERE isCompleted = 1")
    LiveData<Integer> getCompletedModulesCount();
}
