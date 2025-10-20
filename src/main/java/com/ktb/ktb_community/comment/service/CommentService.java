package com.ktb.ktb_community.comment.service;

import com.ktb.ktb_community.comment.dto.response.CommentListResponse;
import com.ktb.ktb_community.comment.repository.CommentRepository;
import com.ktb.ktb_community.global.common.dto.CursorResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Pageable;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;

    // comment 목록 조회
    @Transactional(readOnly = true)
    public CursorResponse<CommentListResponse> getCommentList(Long cursor,
                                                              String deviceType,
                                                              Long postId,
                                                              Long userId
    ) {
        log.info("getCommentList - postId={}, cursor={}", postId, cursor);

        int limit = getPageLimit(deviceType);

        Pageable pageable = PageRequest.of(0, limit+1, Sort.by("id").descending());
        List<CommentListResponse> comments = commentRepository.findCommentListWithCursorAndPostId(cursor, pageable, postId, userId);

        boolean hasNext = comments.size() > limit;
        if(hasNext) {
            comments = comments.subList(0, limit);
        }

        Long nextCursor = hasNext ? comments.get(comments.size()-1).commentId() : null;

        log.info("getCommentList - comments={}", comments.size());
        return new CursorResponse<>(comments, nextCursor, hasNext);
    }

    // 디바이스 타입에 따른 페이징 limit
    private int getPageLimit(String deviceType) {
        int limit = 10;
        switch (deviceType) {
            case "mobile":
                limit = 10;
                break;
            case "tablet":
                limit = 15;
                break;
            case "pc":
                limit = 20;
        }

        return limit;
    }
}
