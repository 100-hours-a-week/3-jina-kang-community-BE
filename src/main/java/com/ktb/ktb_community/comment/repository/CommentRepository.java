package com.ktb.ktb_community.comment.repository;

import com.ktb.ktb_community.comment.dto.response.CommentResponse;
import com.ktb.ktb_community.comment.entity.Comment;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    @Query("""
        SELECT new com.ktb.ktb_community.comment.dto.response.CommentResponse(
            c.id,
            u.nickname,
            c.content,
            c.createdAt,
            CASE WHEN c.user.id = :userId THEN true ELSE false END
        )
        FROM Comment c
        Join c.user u
        WHERE c.deletedAt IS NULL
        AND c.post.id = :postId
        AND (:cursor IS NULL OR c.id < :cursor)
        ORDER BY c.id DESC
        """)
    List<CommentResponse> findCommentListWithCursorAndPostId(
            @Param("cursor") Long cursor,
            Pageable pageable,
            @Param("postId") Long postId,
            @Param("userId") Long userId
    );

    @Query("SELECT COUNT(c) FROM Comment c " +
            "WHERE c.post.id = :postId AND c.deletedAt IS NULL")
    Long countByPostIdAndDeletedAtIsNull(@Param("postId") Long postId);
}
