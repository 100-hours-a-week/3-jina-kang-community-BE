package com.ktb.ktb_community.post.mapper;

import com.ktb.ktb_community.post.dto.request.PostFileRequest;
import com.ktb.ktb_community.post.dto.response.PostFileResponse;
import com.ktb.ktb_community.post.entity.Post;
import com.ktb.ktb_community.post.entity.PostFile;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PostFileMapper {
    public PostFile toEntity(PostFileRequest file, Post post) {
        PostFile postFile = PostFile.builder()
                .post(post)
                .fileName(file.fileName())
                .imageIndex(file.fileOrder())
                .url(file.fileUrl())
                .build();

        return postFile;
    }

    public PostFileResponse toPostFileResponse(PostFile postFile) {
        return new PostFileResponse(
                postFile.getId(),
                postFile.getFileName(),
                postFile.getUrl(),
                postFile.getImageIndex()
        );
    }

    public List<PostFileResponse> toPostFileResponseList(List<PostFile> postFiles) {
        return postFiles.stream()
                .map(this::toPostFileResponse)
                .toList();
    }
}
