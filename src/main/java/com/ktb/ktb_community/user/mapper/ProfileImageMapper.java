package com.ktb.ktb_community.user.mapper;

import com.ktb.ktb_community.global.security.JwtProvider;
import com.ktb.ktb_community.user.dto.request.ProfileImageRequest;
import com.ktb.ktb_community.user.dto.response.ProfileImageResponse;
import com.ktb.ktb_community.user.entity.ProfileImage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProfileImageMapper {

    @Value("${server.base-url}")
    private String serverUrl;

    private final JwtProvider jwtProvider;

    // ProfileImageRequest → ProfileImage 엔티티
    public ProfileImage toEntity(ProfileImageRequest request) {
        return ProfileImage.builder()
                .fileName(request.fileName())
                .url(request.fileUrl())
                .contentType(request.contentType())
                .build();
    }

    // ProfileImage → ProfileImageResponse
    public ProfileImageResponse toProfileImageResponse(ProfileImage profileImage) {
        if (profileImage == null) {
            return null;
        }

        String url;

        if (profileImage.getUrl().startsWith("http")) {
            url = profileImage.getUrl();
        }
        else{
            String profileImageName = profileImage.getUrl();
            String profileImageToken = jwtProvider.createFileToken(profileImageName);
            url = serverUrl + "/api/file/user/" + profileImageName + "?token=" + profileImageToken;
        }

        return new ProfileImageResponse(
                profileImage.getFileName(),
                url,
                profileImage.getContentType()
        );
    }
}
