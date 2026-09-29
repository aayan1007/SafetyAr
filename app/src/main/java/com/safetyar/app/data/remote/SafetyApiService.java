package com.safetyar.app.data.remote;

import com.safetyar.app.data.remote.dto.ApiResponse;
import com.safetyar.app.data.remote.dto.SyncPayloadDto;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface SafetyApiService {

    @POST("api/v1/sync/upload")
    Call<ApiResponse<String>> syncUpload(@Body SyncPayloadDto payload);

    @GET("api/v1/workers/{id}/status")
    Call<ApiResponse<String>> getWorkerComplianceStatus(@Path("id") String workerId);

    @GET("api/v1/certificates/verify/{hash}")
    Call<ApiResponse<Boolean>> verifyCertificateHashOnline(@Path("hash") String certHash);
}
