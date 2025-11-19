package com.ktb.ktb_community.global.file.service;

import com.ktb.ktb_community.global.exception.CustomException;
import com.ktb.ktb_community.global.exception.ErrorCode;
import com.ktb.ktb_community.global.security.JwtProvider;
import com.ktb.ktb_community.post.repository.PostFileRepository;
import com.ktb.ktb_community.user.repository.ProfileImageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;

import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j
@Service
@RequiredArgsConstructor
public class LocalFileService implements FileService {

    @Value("${file.upload.path}")
    private String uploadPath;

    private final PostFileRepository postFileRepository;
    private final ProfileImageRepository profileImageRepository;
    private final JwtProvider jwtProvider;

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

    @Override
    public Resource getFileWithToken(String fileName, String token) {
        log.info("file token - token:{}", token);

        if(token == null || token.isEmpty()){
            throw new CustomException(ErrorCode.INVALID_FILE_TOKEN);
        }
        if(!jwtProvider.validateToken(token)){
            throw new CustomException(ErrorCode.INVALID_FILE_TOKEN);
        }
        if(!jwtProvider.isFileToken(token)){
            throw new CustomException(ErrorCode.INVALID_FILE_TOKEN);
        }

        String tokenfileName = jwtProvider.getFileNameFromToken(token);
        if(!tokenfileName.equals(fileName)) {
            throw new CustomException(ErrorCode.INVALID_FILE_TOKEN);
        }

        return getFile(fileName);
    }
}
