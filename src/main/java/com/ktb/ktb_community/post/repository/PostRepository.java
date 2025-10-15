package com.ktb.ktb_community.post.repository;

import com.ktb.ktb_community.post.dto.response.PostListResponse;
import com.ktb.ktb_community.post.entity.Post;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    @Query("""
        SELECT new com.ktb.ktb_community.post.dto.response.PostListResponse(
            p.id,
            p.title,
            u.nickname,
            ps.likeCount,
            COUNT(DISTINCT c.id),
            ps.viewCount,
            p.createdAt
        )
        FROM Post p
        JOIN p.user u
        LEFT JOIN PostStatus ps ON ps.postId = p.id
        LEFT JOIN Comment c ON c.post.id = p.id AND c.deletedAt IS NULL
        WHERE p.deletedAt IS NULL
        AND (:cursor IS NULL OR p.id < :cursor)
        GROUP BY p.id, p.title, u.nickname, ps.likeCount, ps.viewCount, p.createdAt
        ORDER BY p.id DESC
        """)
    List<PostListResponse> findPostListWithCursor(
            @Param("cursor") Long cursor,
            Pageable pageable
    );
}
