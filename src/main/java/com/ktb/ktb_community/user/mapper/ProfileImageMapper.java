package com.ktb.ktb_community.user.mapper;

import com.ktb.ktb_community.global.security.JwtProvider;
import com.ktb.ktb_community.user.dto.request.ProfileImageRequest;
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

    // ProfileImage → URL 변환
    public String toProfileImageUrl(ProfileImage profileImage) {
        if (profileImage == null) {
            return getDefaultProfileImageUrl();
        }

        String url = profileImage.getUrl();

        if (url.startsWith("http")) {
            return url;
        }

        String fileToken = jwtProvider.createFileToken(url);
        return serverUrl + "/api/file/" + url + "?token=" + fileToken;
    }

    private String getDefaultProfileImageUrl() {
        return serverUrl + "/images/default-profile.png";
    }
}
