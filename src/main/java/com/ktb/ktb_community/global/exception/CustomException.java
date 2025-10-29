package com.ktb.ktb_community.global.exception;

import lombok.Getter;

@Getter
public class CustomException extends RuntimeException {
    private  final ErrorCode errorCode;

    // ENUM 메세지
    public CustomException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    // 동적 메세지
    public CustomException(ErrorCode errorCode, String errorMessage) {
        super(errorMessage);
        this.errorCode = errorCode;
    }
}
