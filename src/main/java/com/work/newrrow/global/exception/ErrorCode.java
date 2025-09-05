package com.work.newrrow.global.exception;

import org.springframework.http.HttpStatus;

public enum ErrorCode {
    // User 관련
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."),
    ALREADY_COMPLETED(HttpStatus.BAD_REQUEST, "이미 존재합니다."),

    // ==== Group 관련 ====
    GROUP_NOT_FOUND(HttpStatus.NOT_FOUND, "그룹을 찾을 수 없습니다."),
    INVALID_GROUP_CODE(HttpStatus.BAD_REQUEST, "유효하지 않은 그룹 코드입니다."),
    PERMISSION_DENIED(HttpStatus.FORBIDDEN, "권한이 없습니다."),
    GROUP_FULL(HttpStatus.CONFLICT, "그룹 정원이 가득 찼습니다."),
    GROUP_EXPIRED(HttpStatus.BAD_REQUEST, "만료된 그룹입니다."),

    // ==== Quest 관련 ====
    QUEST_NOT_FOUND(HttpStatus.NOT_FOUND, "퀘스트를 찾을 수 없습니다."),
    QUEST_LIMIT_EXCEEDED(HttpStatus.BAD_REQUEST, "퀘스트 개수 제한을 초과했습니다."),
    ALREADY_COMPLETED_QUEST(HttpStatus.BAD_REQUEST, "이미 완료된 퀘스트입니다.");

    private final HttpStatus status;
    private final String defaultMessage;

    ErrorCode(HttpStatus status, String defaultMessage) {
        this.status = status;
        this.defaultMessage = defaultMessage;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public String getDefaultMessage() {
        return defaultMessage;
    }
}