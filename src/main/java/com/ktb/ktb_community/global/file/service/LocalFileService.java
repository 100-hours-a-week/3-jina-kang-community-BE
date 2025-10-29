package com.ktb.ktb_community.global.file.service;

import com.ktb.ktb_community.global.exception.CustomException;
import com.ktb.ktb_community.global.exception.ErrorCode;
import com.ktb.ktb_community.global.file.dto.response.UploadFileResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
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
            if (originalFilename == null || originalFilename.isEmpty()) {
                throw new CustomException(ErrorCode.FILE_NOT_FOUND);
            }
            if (!originalFilename.contains(".")) {
                throw new CustomException(ErrorCode.INVALID_FILE);
            }
            String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            String fileName = UUID.randomUUID().toString() + extension;
            // 파일 저장
            File uploadFile = new File(uploadDir, fileName);
            file.transferTo(uploadFile);
            // 파일 타입
            String type = file.getContentType();
            if(type == null || type.isEmpty()){
                throw new CustomException(ErrorCode.INVALID_FILE);
            }

            return new UploadFileResponse(
                    originalFilename,
                    fileName,
                    type
            );
        } catch (IOException e) {
            throw new CustomException(ErrorCode.FILE_UPLOAD_FAIL);
        }
    }

    // 파일 조회
    @Override
    public Resource getFile(String fileUrl) {
        try {
            log.info("file upload - file:{}", fileUrl);
            // 파일 검증
            if(fileUrl == null || fileUrl.isEmpty()){
                throw new CustomException(ErrorCode.FILE_NOT_FOUND);
            }
            if(fileUrl.contains("..") || fileUrl.contains("/") || fileUrl.contains("\\")){
                throw new CustomException(ErrorCode.INVALID_FILE);
            }
            // 경로
            Path path = Paths.get(uploadPath).resolve(fileUrl).normalize();
            Resource resource = new UrlResource(path.toUri());
            // 파일 존재 확인
            if(!resource.exists() || !resource.isReadable()){
                throw new CustomException(ErrorCode.FILE_NOT_FOUND);
            }

            return resource;
        } catch (MalformedURLException e) {
            throw new CustomException(ErrorCode.FILE_NOT_FOUND);
        }
    }
}
