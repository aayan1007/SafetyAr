package com.safetyar.app.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.safetyar.app.data.local.entity.ARScenarioEntity;

import java.util.List;

@Dao
public interface ARScenarioDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertScenarios(List<ARScenarioEntity> scenarios);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertScenario(ARScenarioEntity scenario);

    @Query("SELECT * FROM ar_scenarios WHERE sector = :sector")
    LiveData<List<ARScenarioEntity>> getScenariosBySector(String sector);

    @Query("SELECT * FROM ar_scenarios WHERE moduleId = :moduleId LIMIT 1")
    LiveData<ARScenarioEntity> getScenarioByModuleId(String moduleId);

    @Query("SELECT * FROM ar_scenarios WHERE scenarioId = :scenarioId LIMIT 1")
    ARScenarioEntity getScenarioByIdSync(String scenarioId);
}
