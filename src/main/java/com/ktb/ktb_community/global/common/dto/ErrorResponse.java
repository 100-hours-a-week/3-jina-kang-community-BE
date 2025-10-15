package com.ktb.ktb_community.global.common.dto;

import com.ktb.ktb_community.global.exception.ErrorCode;
import com.ktb.ktb_community.global.exception.dto.ErrorDetail;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Map;

@Getter
@AllArgsConstructor
@Builder
public class ErrorResponse {
    private boolean success;
    private ErrorDetail error;
    private LocalDateTime timestamp;

    // ENUM ErrorCode 사용
    public static ErrorResponse of(ErrorCode errorCode) {
        return ErrorResponse.builder()
                .success(false)
                .error(ErrorDetail.builder()
                        .code(errorCode.name())
                        .message(errorCode.getMessage())
                        .status(errorCode.getStatus().value())
                        .build())
                .timestamp(LocalDateTime.now())
                .build();
    }

    // 에러 메세지 동적으로 지정
    public static ErrorResponse of(ErrorCode errorCode, String message) {
        return ErrorResponse.builder()
                .success(false)
                .error(ErrorDetail.builder()
                    .code(errorCode.name())
                    .message(message)
                    .status(errorCode.getStatus().value())
                    .build())
                .timestamp(LocalDateTime.now())
                .build();
    }

    // Valid용
    public static ErrorResponse of(ErrorCode errorCode, Map<String, String> details) {
        return ErrorResponse.builder()
                .success(false)
                .error(ErrorDetail.builder()
                        .code(errorCode.name())
                        .message(errorCode.getMessage())
                        .status(errorCode.getStatus().value())
                        .details(details)
                        .build())
                .timestamp(LocalDateTime.now())
                .build();
    }
}
