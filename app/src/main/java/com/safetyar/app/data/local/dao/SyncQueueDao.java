package com.safetyar.app.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.safetyar.app.data.local.entity.SyncQueueEntity;

import java.util.List;

@Dao
public interface SyncQueueDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void enqueue(SyncQueueEntity item);

    @Update
    void updateItem(SyncQueueEntity item);

    @Query("SELECT * FROM sync_queue WHERE status = 'PENDING' ORDER BY createdAt ASC")
    List<SyncQueueEntity> getPendingSyncItems();

    @Query("SELECT COUNT(*) FROM sync_queue WHERE status = 'PENDING'")
    LiveData<Integer> getPendingSyncCount();

    @Query("DELETE FROM sync_queue WHERE syncId = :syncId")
    void deleteBySyncId(String syncId);

    @Query("DELETE FROM sync_queue WHERE status = 'COMPLETED'")
    void clearCompleted();
}
