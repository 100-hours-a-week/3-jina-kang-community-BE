package com.ktb.ktb_community.post.service;

import com.ktb.ktb_community.global.common.dto.CursorResponse;
import com.ktb.ktb_community.global.exception.CustomException;
import com.ktb.ktb_community.global.exception.ErrorCode;
import com.ktb.ktb_community.post.dto.request.PostCreateRequest;
import com.ktb.ktb_community.post.dto.request.PostUpdateRequest;
import com.ktb.ktb_community.post.dto.response.PostDetailResponse;
import com.ktb.ktb_community.post.dto.response.PostListResponse;
import com.ktb.ktb_community.post.entity.Post;
import com.ktb.ktb_community.post.entity.PostStatus;
import com.ktb.ktb_community.post.mapper.PostMapper;
import com.ktb.ktb_community.post.repository.PostRepository;
import com.ktb.ktb_community.post.repository.PostStatusRepository;
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
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final PostStatusRepository postStatusRepository;
    private final PostMapper postMapper;

    // post 목록 조회
    @Transactional(readOnly = true)
    public CursorResponse<PostListResponse> getPostList(Long cursor, String deviceType) {
        if(cursor != null && cursor <= 0) cursor = null;
        log.info("getPostList - cursor: {}", cursor);

        int limit = getPageLimit(deviceType);

        Pageable pageable = PageRequest.of(0, limit+1, Sort.by("id").descending());
        List<PostListResponse> posts = postRepository.findPostListWithCursor(cursor, pageable);

        boolean hasNext = posts.size() > limit;
        if(hasNext) {
            posts = posts.subList(0, limit);
        }

        Long nextCursor = hasNext ? posts.get(posts.size()-1).postId() : null;

        log.info("getPostList - posts: {}", posts.size());
        return new CursorResponse<>(posts, nextCursor, hasNext);
    }

    // post 상세 조회
    @Transactional
    public PostDetailResponse getPostDetail(Long postId, Long userId) {
        log.info("getPostDetail - {}", postId);

        Post post = postRepository.findByIdWithUserAndPostStatus(postId)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));

        // viewCount 증가
        PostStatus postStatus = post.getPostStatus();
        if(postStatus != null) {
            postStatus.incrementViewCount();
        }
        PostDetailResponse response = postMapper.toPostDetailResponse(post, userId);

        return response;
    }

    // post 생성
    @Transactional
    public PostDetailResponse createPost(PostCreateRequest request, Long userId) {
        log.info("createPost - {}", request);

        // 작성자 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.UNAUTHORIZED));
        // DTO -> Entity
        Post post = postMapper.toEntity(request,  user);
        // 게시물 저장
        Post savedPost = postRepository.save(post);
        // TODO 사진 저장

        // 저장된 게시물 반환
        // Entity -> DTO
        PostDetailResponse response = postMapper.toPostDetailResponse(savedPost, userId);

        return response;
    }

    @Transactional
    public PostDetailResponse updatePost(Long postId, PostUpdateRequest request, Long userId) {
        log.info("updatePost - {}", postId);

        // 게시글 조회
        Post post = postRepository.findByIdWithUserAndPostStatus(postId)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));
        // 본인의 글인지 확인
        if(!post.getUser().getId().equals(userId)) {
            throw new CustomException(ErrorCode.FORBIDDEN);
        }
        // 게시글 업데이트
        post.updatePost(request.title(), request.content());
        // TODO 업데이트된 파일 리스트와 기존 파일 비교 - 변경된 파일 수정

        // 업데이트한 게시글 상세조회 데이터 조회
        PostDetailResponse response = postMapper.toPostDetailResponse(post, userId);

        return response;
    }

    // post 삭제
    @Transactional
    public void deletePost(Long postId, Long userId) {
        log.info("deletePost - {}", postId);

        // 게시글 조회
        Post post = postRepository.findByIdWithUserAndPostStatus(postId)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));
        // 본인 글인지 확인
        if(!post.getUser().getId().equals(userId)) {
            throw new CustomException(ErrorCode.FORBIDDEN);
        }
        // soft delete
        post.delete();
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
