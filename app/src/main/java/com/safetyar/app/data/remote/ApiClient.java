package com.safetyar.app.data.remote;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    // Default Central Portal Endpoint (DGMS / Jharkhand Industrial Safety Council)
    public static final String DEFAULT_CLOUD_URL = "https://safetyar-dgms-jharkhand.gov.in/";
    // Local development loopback for Android Emulator testing
    public static final String LOCAL_EMULATOR_URL = "http://10.0.2.2:5000/";

    private static String currentBaseUrl = DEFAULT_CLOUD_URL;
    private static Retrofit retrofit = null;

    public static synchronized void setBaseUrl(String newUrl) {
        if (newUrl != null && !newUrl.trim().isEmpty()) {
            if (!newUrl.endsWith("/")) newUrl += "/";
            currentBaseUrl = newUrl;
            retrofit = null; // Rebuild client on next call
        }
    }

    public static synchronized String getBaseUrl() {
        return currentBaseUrl;
    }

    public static synchronized Retrofit getClient() {
        if (retrofit == null) {
            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(interceptor)
                    .connectTimeout(10, TimeUnit.SECONDS)
                    .readTimeout(10, TimeUnit.SECONDS)
                    .writeTimeout(10, TimeUnit.SECONDS)
                    .retryOnConnectionFailure(true)
                    .build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(currentBaseUrl)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    public static SafetyApiService getApiService() {
        return getClient().create(SafetyApiService.class);
    }
}
