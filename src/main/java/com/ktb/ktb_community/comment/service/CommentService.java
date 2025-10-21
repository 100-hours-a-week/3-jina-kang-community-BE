package com.ktb.ktb_community.comment.service;

import com.ktb.ktb_community.comment.dto.request.CommentCreateRequest;
import com.ktb.ktb_community.comment.dto.request.CommentUpdateRequest;
import com.ktb.ktb_community.comment.dto.response.CommentResponse;
import com.ktb.ktb_community.comment.entity.Comment;
import com.ktb.ktb_community.comment.mapper.CommentMapper;
import com.ktb.ktb_community.comment.repository.CommentRepository;
import com.ktb.ktb_community.global.common.dto.CursorResponse;
import com.ktb.ktb_community.global.exception.CustomException;
import com.ktb.ktb_community.global.exception.ErrorCode;
import com.ktb.ktb_community.post.entity.Post;
import com.ktb.ktb_community.post.repository.PostRepository;
import com.ktb.ktb_community.user.entity.User;
import com.ktb.ktb_community.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final CommentMapper commentMapper;

    // comment 목록 조회
    @Transactional(readOnly = true)
    public CursorResponse<CommentResponse> getCommentList(Long cursor,
                                                          String deviceType,
                                                          Long postId,
                                                          Long userId
    ) {
        log.info("getCommentList - postId={}, cursor={}", postId, cursor);

        int limit = getPageLimit(deviceType);

        Pageable pageable = PageRequest.of(0, limit+1, Sort.by("id").descending());
        List<CommentResponse> comments = commentRepository.findCommentListWithCursorAndPostId(cursor, pageable, postId, userId);

        boolean hasNext = comments.size() > limit;
        if(hasNext) {
            comments = comments.subList(0, limit);
        }

        Long nextCursor = hasNext ? comments.get(comments.size()-1).commentId() : null;

        log.info("getCommentList - comments={}", comments.size());
        return new CursorResponse<>(comments, nextCursor, hasNext);
    }

    // comment 생성
    @Transactional
    public CommentResponse createComment(CommentCreateRequest request,
                                         Long postId,
                                         Long userId) {
        log.info("createComment - postId={}, request={}", postId, request);
        // 작성자 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.UNAUTHORIZED));
        // 게시물 조회
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));
        // DTO -> Entity
        Comment comment = commentMapper.toEntity(request, user, post);
        // 댓글 저장
        Comment savedComment = commentRepository.save(comment);
        // Entity -> DTO
        CommentResponse response = commentMapper.toCommentResponse(savedComment, userId);
        // 수정된 comment 반환
        return response;
    }

    // comment 수정
    @Transactional
    public CommentResponse updateComment(CommentUpdateRequest request, Long commentId, Long userId) {
        log.info("deleteComment - request={}", request);

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CustomException(ErrorCode.COMMENT_NOT_FOUND));
        // 본인 댓글인지 확인
        if(comment.getUser().getId().equals(userId)) {
            throw new CustomException(ErrorCode.FORBIDDEN);
        }
        // 댓글 업데이트
        comment.update(request.content());
        // DTO -> Entity
        CommentResponse response = commentMapper.toCommentResponse(comment, userId);
        // 반환
        return response;
    }

    // 댓글 삭제
    @Transactional
    public void deleteComment(Long commentId, Long userId) {
        log.info("deleteComment - commentId={}", commentId);

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CustomException(ErrorCode.COMMENT_NOT_FOUND));
        // 본인 댓글인지 확인
        if(comment.getUser().getId().equals(userId)) {
            throw new CustomException(ErrorCode.FORBIDDEN);
        }
        // soft delete
        comment.delete();
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
