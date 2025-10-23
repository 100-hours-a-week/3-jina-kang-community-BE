package com.ktb.ktb_community.post.repository;

import com.ktb.ktb_community.post.entity.PostFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostFileRepository extends JpaRepository<PostFile, Long> {

    List<PostFile> findByPostIdOrderByImageIndexAsc(Long postId);

    List<PostFile> findByPostIdAndDeletedAtIsNullOrderByImageIndexAsc(Long postId);

}
