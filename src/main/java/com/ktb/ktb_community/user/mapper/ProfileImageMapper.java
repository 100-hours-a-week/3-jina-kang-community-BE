package com.ktb.ktb_community.user.mapper;

import com.ktb.ktb_community.user.dto.request.ProfileImageRequest;
import com.ktb.ktb_community.user.dto.response.ProfileImageResponse;
import com.ktb.ktb_community.user.entity.ProfileImage;
import org.springframework.stereotype.Component;

@Component
public class ProfileImageMapper {

    // ProfileImageRequest → ProfileImage 엔티티
    public ProfileImage toEntity(ProfileImageRequest request) {
        return ProfileImage.builder()
                .fileName(request.fileName())
                .fileKey(request.fileKey())
                .url(request.s3Url())
                .contentType(request.contentType())
                .build();
    }

    // ProfileImage → ProfileImageResponse
    public ProfileImageResponse toProfileImageResponse(ProfileImage profileImage) {
        if (profileImage == null) {
            return null;
        }

        // S3/CloudFront URL을 그대로 반환
        return new ProfileImageResponse(
                profileImage.getFileName(),
                profileImage.getUrl(),
                profileImage.getContentType()
        );
    }
}
