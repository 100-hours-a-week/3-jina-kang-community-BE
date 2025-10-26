package com.ktb.ktb_community.post.mapper;

import com.ktb.ktb_community.global.file.entity.FileType;
import com.ktb.ktb_community.global.security.JwtProvider;
import com.ktb.ktb_community.post.dto.request.PostFileRequest;
import com.ktb.ktb_community.post.dto.response.PostFileResponse;
import com.ktb.ktb_community.post.entity.Post;
import com.ktb.ktb_community.post.entity.PostFile;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class PostFileMapper {

    @Value("${server.base-url}")
    private String serverUrl;

    private final JwtProvider jwtProvider;

    public PostFile toEntity(PostFileRequest file, Post post) {
        PostFile postFile = PostFile.builder()
                .post(post)
                .fileName(file.fileName())
                .imageIndex(file.fileOrder())
                .url(file.fileUrl())
                .contentType(file.contentType())
                .build();

        return postFile;
    }

    public PostFileResponse toPostFileResponse(PostFile postFile) {
        String url;

        if(postFile.getUrl().startsWith("http")) {
            url = postFile.getUrl();
        }
        else {
            String fileName = postFile.getUrl();
            String fileToken = jwtProvider.createFileToken(fileName);
            url = serverUrl + "/api/file/" + fileName + "?token=" + fileToken;
        }

        return new PostFileResponse(
                postFile.getId(),
                postFile.getFileName(),
                url,
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
