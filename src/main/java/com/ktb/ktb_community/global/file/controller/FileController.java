package com.ktb.ktb_community.global.file.controller;

import com.ktb.ktb_community.global.file.service.FileService;
import com.ktb.ktb_community.post.service.PostService;
import com.ktb.ktb_community.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/file")
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;
    private final PostService postService;
    private final UserService userService;

    // 게시글 파일 조회
    @GetMapping("/post/{fileName}")
    public ResponseEntity<Resource> getFile(
            @PathVariable String fileName,
            @RequestParam String token
    ) {
        log.info("file upload");

        Resource resource = postService.getPostFile(fileName, token);
        String contentType = postService.getPostFileContentType(fileName);

        return ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType)).body(resource);
    }

    // 프로필사진 이미지 조회
    @GetMapping("/user/{fileName}")
    public ResponseEntity<Resource> getProfileImage(
            @PathVariable String fileName,
            @RequestParam String token
    ) {
        log.info("get profile image: {}", fileName);

        Resource resource = userService.getProfileImage(fileName, token);
        String contentType = userService.getProfileImageContentType(fileName);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .body(resource);
    }
}
