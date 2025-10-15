package com.ktb.ktb_community.global.common.dto;

import java.util.List;

public record CursorResponse<T> (
        List<T> data,
        Long nextCursor,
        boolean hasNext
){}
