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
                .fileKey(file.fileKey())
                .imageIndex(file.fileOrder())
                .url(file.s3Url())
                .contentType(file.contentType())
                .build();

        return postFile;
    }

    public PostFileResponse toPostFileResponse(PostFile postFile) {
        // S3/CloudFront URL을 그대로 반환
        return new PostFileResponse(
                postFile.getId(),
                postFile.getFileName(),
                postFile.getUrl(),
                postFile.getImageIndex(),
                postFile.getContentType()
        );
    }

    public List<PostFileResponse> toPostFileResponseList(List<PostFile> postFiles) {
        return postFiles.stream()
                .map(this::toPostFileResponse)
                .toList();
    }
}
