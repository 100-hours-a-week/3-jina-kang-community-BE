package com.ktb.ktb_community.post.repository;

import com.ktb.ktb_community.post.entity.PostStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostStatusRepository extends JpaRepository<PostStatus, Long> {
}
