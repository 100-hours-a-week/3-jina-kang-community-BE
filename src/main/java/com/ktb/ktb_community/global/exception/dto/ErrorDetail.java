package com.ktb.ktb_community.global.exception.dto;

import lombok.Builder;

import java.util.Map;

@Builder
public record ErrorDetail (
        String code,
        String message,
        int status,
        Map<String, String> details
) {}
