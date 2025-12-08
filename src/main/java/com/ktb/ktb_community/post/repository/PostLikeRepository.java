package com.ktb.ktb_community.post.repository;

import com.ktb.ktb_community.post.entity.PostLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PostLikeRepository extends JpaRepository<PostLike, Long> {

    // 좋아요 존재 여부 확인
    boolean existsByPostIdAndUserId(Long postId, Long userId);

    // 좋아요 조회
    Optional<PostLike> findByPostIdAndUserId(Long postId, Long userId);

    // 좋아요 삭제
    void deleteByPostIdAndUserId(Long postId, Long userId);


}
