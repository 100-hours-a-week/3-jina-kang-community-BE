package com.ktb.ktb_community.global.file.controller;

import com.ktb.ktb_community.global.common.dto.ApiResponse;
import com.ktb.ktb_community.global.file.dto.FileInfo;
import com.ktb.ktb_community.global.file.dto.response.UploadFileResponse;
import com.ktb.ktb_community.global.file.service.FileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequestMapping("/api/file")
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;

    @PostMapping
    public ResponseEntity<ApiResponse<UploadFileResponse>> uploadFile(MultipartFile file) {
        log.info("file upload");

        UploadFileResponse response = fileService.upLoadFile(file);
        ApiResponse<UploadFileResponse> apiResponse = ApiResponse.success(response);

        return ResponseEntity.ok(apiResponse);
    }
}
