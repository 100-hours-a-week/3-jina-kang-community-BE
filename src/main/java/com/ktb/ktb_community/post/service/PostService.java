package com.ktb.ktb_community.post.service;

import com.ktb.ktb_community.global.common.dto.CursorResponse;
import com.ktb.ktb_community.post.dto.response.PostListResponse;
import com.ktb.ktb_community.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;

    @Transactional(readOnly = true)
    public CursorResponse<PostListResponse> getPostList(Long cursor, int limit) {
        log.info("getPostList - cursor: {}", cursor);

        Pageable pageable = PageRequest.of(0, limit+1, Sort.by("id").descending());
        List<PostListResponse> posts = postRepository.findPostListWithCursor(cursor, pageable);

        boolean hasNext = posts.size() > limit;
        if(hasNext) {
            posts = posts.subList(0, limit);
        }

        Long nextCursor = hasNext ? posts.get(posts.size()-1).postId() : null;

        return new CursorResponse<>(posts, nextCursor, hasNext);
    }

}
