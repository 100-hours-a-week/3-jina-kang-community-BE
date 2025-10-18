package com.ktb.ktb_community.user.service;

import com.ktb.ktb_community.global.exception.CustomException;
import com.ktb.ktb_community.global.exception.ErrorCode;
import com.ktb.ktb_community.user.dto.request.SignupRequest;
import com.ktb.ktb_community.user.entity.ProfileImage;
import com.ktb.ktb_community.user.entity.User;
import com.ktb.ktb_community.user.mapper.UserMapper;
import com.ktb.ktb_community.user.repository.ProfileImageRepository;
import com.ktb.ktb_community.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final ProfileImageRepository profileImageRepository;
    private final UserMapper userMapper;

    public void signup(SignupRequest signupRequest) {
        log.info("signup email: {}", signupRequest.email());

        // 이메일 중복체크
        if(userRepository.existsByEmail(signupRequest.email())) {
            throw new CustomException(ErrorCode.EXISTED_EMAIL);
        }

        // 닉네임 중복 체크
        if(userRepository.existsByNickname(signupRequest.nickname())) {
            throw new CustomException(ErrorCode.EXISTED_NICKNAME);
        }

        // 프로필 이미지
        ProfileImage profileImage = profileImageRepository.findById(signupRequest.profileImageId())
                .orElseThrow(() -> new CustomException(ErrorCode.PROFILE_IMAGE_NOT_FOUND));

        User user = userMapper.toEntity(signupRequest, profileImage);

        User savedUser = userRepository.save(user);
    }
}
