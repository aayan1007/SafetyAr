package com.safetyar.app.data.remote.dto;

public class ApiResponse<T> {
    private boolean success;
    private String message;
    private T data;
    private long serverTimestamp;

    public ApiResponse() {
    }

    public ApiResponse(boolean success, String message, T data, long serverTimestamp) {
        this.success = success;
        this.message = message;
        this.data = data;
        this.serverTimestamp = serverTimestamp;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public long getServerTimestamp() {
        return serverTimestamp;
    }

    public void setServerTimestamp(long serverTimestamp) {
        this.serverTimestamp = serverTimestamp;
    }
}
