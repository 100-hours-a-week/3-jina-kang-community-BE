package com.ktb.ktb_community.post.service;

import com.ktb.ktb_community.global.common.dto.CursorResponse;
import com.ktb.ktb_community.global.exception.CustomException;
import com.ktb.ktb_community.global.exception.ErrorCode;
import com.ktb.ktb_community.post.dto.request.PostCreateRequest;
import com.ktb.ktb_community.post.dto.request.PostFileRequest;
import com.ktb.ktb_community.post.dto.request.PostUpdateRequest;
import com.ktb.ktb_community.post.dto.response.PostDetailResponse;
import com.ktb.ktb_community.post.dto.response.PostFileResponse;
import com.ktb.ktb_community.post.dto.response.PostListResponse;
import com.ktb.ktb_community.post.entity.Post;
import com.ktb.ktb_community.post.entity.PostFile;
import com.ktb.ktb_community.post.entity.PostStatus;
import com.ktb.ktb_community.post.mapper.PostFileMapper;
import com.ktb.ktb_community.post.mapper.PostMapper;
import com.ktb.ktb_community.post.repository.PostFileRepository;
import com.ktb.ktb_community.post.repository.PostLikeRepository;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final PostStatusRepository postStatusRepository;
    private final PostLikeRepository postLikeRepository;
    private final PostFileRepository postFileRepository;
    private final PostMapper postMapper;
    private final PostFileMapper postFileMapper;

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
        return new CursorResponse<PostListResponse>(posts, nextCursor, hasNext, null);
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
        // postFile 조회
        List<PostFile> postFiles = postFileRepository.findByPostIdAndDeletedAtIsNullOrderByImageIndexAsc(postId);
        // postFile Entity -> dto
        List<PostFileResponse> fileResponses = postFileMapper.toPostFileResponseList(postFiles);
        // 좋아요 여부 확인
        boolean isLiked = userId != null && postLikeRepository.existsByPostIdAndUserId(postId, userId);
        // 응답 dto
        PostDetailResponse response = postMapper.toPostDetailResponse(post, fileResponses, userId, isLiked);

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
        // 사진 저장
        // 첨부된 파일이 너무 많으면 예외처리 (최대 5장)
        if(request.postImages().size() > 5) {
            throw new CustomException(ErrorCode.TOO_MANY_FILES);
        }
        List<PostFileResponse> savedPostFileList = new ArrayList<>();
        if (request.postImages() != null && !request.postImages().isEmpty()) {
            for (PostFileRequest file : request.postImages()) {
                PostFile postFile = postFileMapper.toEntity(file, post);
                PostFile savedPostFile = postFileRepository.save(postFile);
                PostFileResponse fileResponse = postFileMapper.toPostFileResponse(savedPostFile);
                savedPostFileList.add(fileResponse);
            }
        }
        // 저장된 게시물 반환
        // Entity -> DTO
        boolean isLiked = false; // 새로 생성한 게시글 -> 좋아요 전
        PostDetailResponse response = postMapper.toPostDetailResponse(savedPost, savedPostFileList, userId, isLiked);

        return response;
    }

    // post 수정
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
        // 업데이트된 파일 리스트와 기존 파일 비교 - 변경된 파일 수정
        // 첨부된 파일이 너무 많으면 예외처리 (최대 5장)
        if(request.postImages().size() > 5) {
            throw new CustomException(ErrorCode.TOO_MANY_FILES);
        }
        if(request.postImages() != null && !request.postImages().isEmpty()) {
            updatePostFiles(post, request.postImages());
        }
        // 업데이트한 postFileList 조회
        List<PostFile> savedPostFileList = postFileRepository
                .findByPostIdAndDeletedAtIsNullOrderByImageIndexAsc(postId);
        List<PostFileResponse> savedPostFileResponseList = postFileMapper
                .toPostFileResponseList(savedPostFileList);
        // 좋아요 여부 확인
        boolean isLiked = postLikeRepository.existsByPostIdAndUserId(postId, userId);
        // 업데이트한 게시글 상세조회 데이터 조회
        PostDetailResponse response = postMapper.toPostDetailResponse(post, savedPostFileResponseList, userId, isLiked);

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

    private void updatePostFiles(Post post, List<PostFileRequest> postFiles) {
        List<PostFile> existingFiles = postFileRepository
                .findByPostIdAndDeletedAtIsNullOrderByImageIndexAsc(post.getId());

        Map<String, PostFile> fileMap = existingFiles.stream()
                .collect(Collectors.toMap(PostFile::getFileKey, f -> f));

        log.info("=== 파일 업데이트 시작 ===");
        log.info("기존 파일 개수: {}", existingFiles.size());
        log.info("요청 파일 개수: {}", postFiles.size());

        log.info("파일 맵 생성:");
        fileMap.forEach((key, value) -> log.info("  - {}: ID={}", key, value.getId()));

        // 삭제 체크
        Set<String> requestedFileKeys = postFiles.stream()
                .map(f -> extractFileKey(f.s3Url()))
                .collect(Collectors.toSet());

        for(PostFile existingFile : existingFiles) {
            if(!requestedFileKeys.contains(existingFile.getFileKey())) {
                log.info("삭제할 파일: {}", existingFile.getFileKey());
                existingFile.changeToDeleted();
                fileMap.remove(existingFile.getFileKey());  // Map에서도 제거
            }
        }

        log.info("삭제 처리 후 파일 맵:");
        fileMap.forEach((key, value) -> log.info("  - {}: ID={}", key, value.getId()));

        // 파일 처리
        for(int i = 0; i < postFiles.size(); i++) {
            PostFileRequest file = postFiles.get(i);

            String fileKey = extractFileKey(file.s3Url());
            log.info("처리 중 - 파일 {}: {}", i+1, fileKey);

            PostFile existingFile = fileMap.get(fileKey);  // ← Map에서 직접 조회

            log.info("Map에서 찾은 파일: {}", existingFile != null ? existingFile.getId() : "null");

            if(existingFile != null) {
                log.info("기존 파일 - 순서 업데이트: {} -> {}", existingFile.getImageIndex(), i+1);
                existingFile.updateIndex(i+1);
            } else {
                log.info("새 파일 - 저장 시도: {}", fileKey);

                PostFileRequest newFileRequest = new PostFileRequest(
                        file.fileName(),
                        i + 1,
                        file.fileKey(),
                        file.s3Url(),
                        file.contentType()
                );
                PostFile newFile = postFileMapper.toEntity(newFileRequest, post);
                postFileRepository.save(newFile);
            }
        }
    }

    private String extractFileKey(String s3Url) {
        if(s3Url == null || s3Url.isEmpty()) {
            return null;
        }

        // 이미 fileKey만 있는 경우
        if(!s3Url.startsWith("http")) {
            return s3Url;
        }

        // S3 또는 CloudFront URL에서 fileKey 추출
        // 예: https://cloudfront.domain/path/to/file.jpg -> path/to/file.jpg
        try {
            String path = s3Url.split("\\?")[0]; // 쿼리 파라미터 제거
            int domainEndIndex = path.indexOf("/", 8); // "https://" 이후 첫 번째 /
            if (domainEndIndex != -1) {
                String fileKey = path.substring(domainEndIndex + 1);
                log.info("extractFileKey - 입력: {}", s3Url);
                log.info("extractFileKey - 출력: {}", fileKey);
                return fileKey;
            }
        } catch (Exception e) {
            log.error("extractFileKey 실패: {}", s3Url, e);
        }

        return s3Url;
    }

    // 게시글 파일 조회 - S3/CloudFront 사용으로 더 이상 필요 없음
    // Mapper에서 URL을 직접 반환하므로 이 메서드는 제거 예정
}
