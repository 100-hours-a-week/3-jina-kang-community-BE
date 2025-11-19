package com.ktb.ktb_community.global.file.service;

import org.springframework.core.io.Resource;

public interface FileService {

    Resource getFile(String fileUrl);
    Resource getFileWithToken(String fileName, String token);
}
