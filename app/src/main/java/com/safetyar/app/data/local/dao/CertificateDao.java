package com.safetyar.app.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.safetyar.app.data.local.entity.CertificateEntity;

import java.util.List;

@Dao
public interface CertificateDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertCertificate(CertificateEntity certificate);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertCertificates(List<CertificateEntity> certificates);

    @Update
    void updateCertificate(CertificateEntity certificate);

    @Query("SELECT * FROM certificates WHERE workerId = :workerId ORDER BY issuedTimestamp DESC LIMIT 1")
    LiveData<CertificateEntity> getLatestCertificateByWorker(String workerId);

    @Query("SELECT * FROM certificates WHERE workerId = :workerId ORDER BY issuedTimestamp DESC LIMIT 1")
    CertificateEntity getLatestCertificateByWorkerSync(String workerId);

    @Query("SELECT * FROM certificates WHERE certificateId = :certificateId LIMIT 1")
    LiveData<CertificateEntity> getCertificateById(String certificateId);

    @Query("SELECT * FROM certificates WHERE certificateId = :certificateId LIMIT 1")
    CertificateEntity getCertificateByIdSync(String certificateId);

    @Query("SELECT * FROM certificates ORDER BY issuedTimestamp DESC")
    LiveData<List<CertificateEntity>> getAllCertificates();

    @Query("SELECT * FROM certificates ORDER BY issuedTimestamp DESC")
    List<CertificateEntity> getAllCertificatesSync();

    @Query("UPDATE certificates SET status = :status WHERE certificateId = :certificateId")
    void updateCertificateStatus(String certificateId, String status);

    @Query("SELECT * FROM certificates WHERE isSynced = 0")
    List<CertificateEntity> getUnsyncedCertificates();

    @Query("UPDATE certificates SET isSynced = 1 WHERE certificateId = :certificateId")
    void markAsSynced(String certificateId);
}
