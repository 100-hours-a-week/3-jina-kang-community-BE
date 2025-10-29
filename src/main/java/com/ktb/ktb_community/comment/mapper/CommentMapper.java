package com.ktb.ktb_community.comment.mapper;

import com.ktb.ktb_community.comment.dto.request.CommentCreateRequest;
import com.ktb.ktb_community.comment.dto.response.CommentResponse;
import com.ktb.ktb_community.comment.entity.Comment;
import com.ktb.ktb_community.post.entity.Post;
import com.ktb.ktb_community.user.entity.User;
import org.springframework.stereotype.Component;

@Component
public class CommentMapper {

    // DTO -> Entity
    public Comment toEntity(CommentCreateRequest request, User user, Post post) {
        Comment comment = Comment.builder()
                .post(post)
                .user(user)
                .content(request.content())
                .build();
        return comment;
    }
    
    // Entity -> DTO
    public CommentResponse toCommentResponse(Comment comment, Long userId) {
        boolean isAuthor = userId.equals(comment.getUser().getId());

        return new CommentResponse(
                comment.getId(),
                comment.getUser().getNickname(),
                comment.getContent(),
                comment.getCreatedAt(),
                isAuthor
        );
    }
}
