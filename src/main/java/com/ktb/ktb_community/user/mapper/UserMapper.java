package com.ktb.ktb_community.user.mapper;

import com.ktb.ktb_community.user.dto.request.SignupRequest;
import com.ktb.ktb_community.user.dto.response.UserInfo;
import com.ktb.ktb_community.user.entity.ProfileImage;
import com.ktb.ktb_community.user.entity.User;
import com.ktb.ktb_community.user.entity.UserRole;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    private final PasswordEncoder passwordEncoder;

    public UserMapper(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    public User toEntity(SignupRequest request, ProfileImage profileImage) {
        User user = User.builder()
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .nickname(request.nickname())
                .role(UserRole.USER)
                .profileImage(profileImage)
                .build();

        return user;
    }

    public UserInfo toUserInfo(User user) {
        return new UserInfo(
                user.getId(),
                user.getRole(),
                user.getProfileImage().getUrl()
        );
    }
}
