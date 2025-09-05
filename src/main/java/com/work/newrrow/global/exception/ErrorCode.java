package com.work.newrrow.global.exception;

import org.springframework.http.HttpStatus;


public enum ErrorCode {
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."),
    ALREADY_COMPLETED(HttpStatus.BAD_REQUEST, "이미 존재합니다.");
    public final HttpStatus status; public final String defaultMessage;
    ErrorCode(HttpStatus s, String m){ this.status=s; this.defaultMessage=m; }
}