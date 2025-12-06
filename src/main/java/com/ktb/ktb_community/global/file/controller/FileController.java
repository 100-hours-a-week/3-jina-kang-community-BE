package com.ktb.ktb_community.global.file.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/file")
@RequiredArgsConstructor
public class FileController {

    // S3/CloudFront를 사용하므로 파일 조회 엔드포인트는 더 이상 필요 없음
    // 이미지는 클라이언트에서 CloudFront URL로 직접 접근
}
