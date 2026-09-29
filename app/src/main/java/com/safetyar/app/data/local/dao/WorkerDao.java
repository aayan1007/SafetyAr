package com.safetyar.app.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.safetyar.app.data.local.entity.WorkerEntity;

import java.util.List;

@Dao
public interface WorkerDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertWorker(WorkerEntity worker);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertWorkers(List<WorkerEntity> workers);

    @Update
    void updateWorker(WorkerEntity worker);

    @Query("SELECT * FROM workers WHERE workerId = :workerId LIMIT 1")
    LiveData<WorkerEntity> getWorkerById(String workerId);

    @Query("SELECT * FROM workers WHERE workerId = :workerId LIMIT 1")
    WorkerEntity getWorkerByIdSync(String workerId);

    @Query("SELECT * FROM workers WHERE nationalId = :nationalId OR workerId = :nationalId LIMIT 1")
    WorkerEntity findByNationalOrWorkerIdSync(String nationalId);

    @Query("SELECT * FROM workers WHERE isActiveSession = 1 LIMIT 1")
    LiveData<WorkerEntity> getActiveWorker();

    @Query("SELECT * FROM workers WHERE isActiveSession = 1 LIMIT 1")
    WorkerEntity getActiveWorkerSync();

    @Query("SELECT * FROM workers ORDER BY sector ASC")
    LiveData<List<WorkerEntity>> getAllWorkers();

    @Query("SELECT * FROM workers ORDER BY sector ASC")
    List<WorkerEntity> getAllWorkersSync();

    @Query("UPDATE workers SET isActiveSession = 0")
    void clearAllActiveSessions();

    @Query("UPDATE workers SET isActiveSession = 1 WHERE workerId = :workerId")
    void setActiveWorkerSession(String workerId);

    @Transaction
    default void switchActiveWorker(String workerId) {
        clearAllActiveSessions();
        setActiveWorkerSession(workerId);
    }
}
