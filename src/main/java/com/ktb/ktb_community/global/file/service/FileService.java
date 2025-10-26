package com.ktb.ktb_community.global.file.service;

import com.ktb.ktb_community.global.file.dto.response.UploadFileResponse;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface FileService {

    UploadFileResponse upLoadFile(MultipartFile file);
    Resource getFile(String fileUrl);
    String getContentType(String fileName);
    Resource getFileWithToken(String fileName, String token);
}
