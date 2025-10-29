package com.ktb.ktb_community.post.mapper;

import com.ktb.ktb_community.post.dto.request.PostCreateRequest;
import com.ktb.ktb_community.post.dto.response.PostDetailResponse;
import com.ktb.ktb_community.post.dto.response.PostFileResponse;
import com.ktb.ktb_community.post.entity.Post;
import com.ktb.ktb_community.user.entity.User;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PostMapper {

    // DTO -> Entity
    public Post toEntity(PostCreateRequest request, User user) {
        Post post =  Post.builder()
                .user(user)
                .title(request.title())
                .content(request.content())
                .build();
        // PostStatus 초기화
        post.initPostStatus();

        return post;
    }


    // Entity -> DTO
    public PostDetailResponse toPostDetailResponse(Post post, List<PostFileResponse> postfiles, Long userId) {

        Long viewCount = (post.getPostStatus() != null) ? post.getPostStatus().getViewCount() : 0L;
        Long likeCount = (post.getPostStatus() != null) ? post.getPostStatus().getLikeCount() : 0L;
        boolean isAuthor = userId != null && post.getUser().getId().equals(userId);

        return new PostDetailResponse(
                post.getId(),
                post.getTitle(),
                post.getUser().getNickname(),
                post.getContent(),
                likeCount,
                viewCount,
                post.getCreatedAt(),
                postfiles,
                isAuthor
        );
    }
}
