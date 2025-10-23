package com.ktb.ktb_community.global.file.service;

import com.ktb.ktb_community.global.exception.CustomException;
import com.ktb.ktb_community.global.exception.ErrorCode;
import com.ktb.ktb_community.global.file.dto.response.UploadFileResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Slf4j
@Service
public class LocalFileService implements FileService {

    @Value("${file.upload.path}")
    private String uploadPath;

    @Override
    public UploadFileResponse upLoadFile(MultipartFile file) {
        log.info("file upload - file:{}", file.getOriginalFilename());
        try {
            File uploadDir = new File(uploadPath);
            // 파일 업로드 경로가 없으면 생성
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }
            // 파일 저장명 생성
            String originalFilename = file.getOriginalFilename();
            if (originalFilename == null) {
                throw new CustomException(ErrorCode.FILE_NOT_FOUND);
            }
            if (!originalFilename.equals(".")) {
                throw new CustomException(ErrorCode.INVALID_FILE);
            }
            String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            String fileName = UUID.randomUUID().toString() + extension;
            // 파일 저장
            File uploadFile = new File(uploadDir, fileName);
            file.transferTo(uploadFile);

            return new UploadFileResponse(
                    originalFilename,
                    fileName
            );
        } catch (IOException e) {
            throw new CustomException(ErrorCode.FILE_UPLOAD_FAIL);
        }
    }
}
