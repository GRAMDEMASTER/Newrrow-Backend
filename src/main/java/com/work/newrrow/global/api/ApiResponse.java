package com.work.newrrow.global.api;

public record ApiResponse<T>(boolean success, T data, ApiError error) {
    public static <T> ApiResponse<T> ok(T data){ return new ApiResponse<>(true, data, null); }
}