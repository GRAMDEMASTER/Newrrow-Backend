package com.work.newrrow.global.api;

public record ApiResponse<T>(boolean success, T data, ApiError error) {

    // 성공적인 응답을 생성하는 메서드
    public static <T> ApiResponse<T> ok(T data) {
        return new ApiResponse<>(true, data, null);
    }

    // 오류 응답을 생성하는 메서드 (static으로 추가)
    public static <T> ApiResponse<T> error(String errorMessage) {
        ApiError error = new ApiError(errorMessage);
        return new ApiResponse<>(false, null, error);
    }

    // 오류를 나타내는 ApiError 객체 (간단한 예시로 오류 메시지를 포함)
    public static class ApiError {
        private final String message;

        public ApiError(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }
    }
}
