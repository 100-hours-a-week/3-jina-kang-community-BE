package com.ktb.ktb_community.global.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    // 공통 에러 코드
    VALIDATION_ERROR(HttpStatus.BAD_REQUEST, "입력값 검증에 실패했습니다"),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 에러가 발생했습니다"),
    // auth
    UNAUTHRIZED_USER(HttpStatus.UNAUTHORIZED, "이메일 또는 비밀번호를 잘못 입력했습니다"),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "로그인이 필요합니다"),
    FORBIDDEN(HttpStatus.FORBIDDEN, "접근 권한이 없습니다"),
    // user
    PROFILE_IMAGE_NOT_FOUND(HttpStatus.NOT_FOUND, "프로필 이미지를 찾을 수 없습니다"),
    EXISTED_EMAIL(HttpStatus.CONFLICT, "이미 사용중인 이메일입니다"),
    EXISTED_NICKNAME(HttpStatus.CONFLICT, "이미 사용중인 닉네임입니다"),

    // post
    POST_NOT_FOUND(HttpStatus.NOT_FOUND, "게시글을 찾을 수 없습니다"),
    // comment

    // like

    // file

    ;

    private final HttpStatus status;
    private final String message;

    ErrorCode(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }
}
