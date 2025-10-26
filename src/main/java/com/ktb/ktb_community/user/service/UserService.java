package com.ktb.ktb_community.user.service;

import com.ktb.ktb_community.global.exception.CustomException;
import com.ktb.ktb_community.global.exception.ErrorCode;
import com.ktb.ktb_community.user.dto.request.EmailDuplicationRequest;
import com.ktb.ktb_community.user.dto.request.PasswordEditRequest;
import com.ktb.ktb_community.user.dto.request.ProfileEditRequest;
import com.ktb.ktb_community.user.dto.request.SignupRequest;
import com.ktb.ktb_community.user.dto.response.DuplicationResponse;
import com.ktb.ktb_community.user.dto.response.ProfileResponse;
import com.ktb.ktb_community.user.entity.ProfileImage;
import com.ktb.ktb_community.user.entity.User;
import com.ktb.ktb_community.user.mapper.ProfileImageMapper;
import com.ktb.ktb_community.user.mapper.UserMapper;
import com.ktb.ktb_community.user.repository.ProfileImageRepository;
import com.ktb.ktb_community.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final ProfileImageRepository profileImageRepository;
    private final UserMapper userMapper;
    private final ProfileImageMapper profileImageMapper;
    private final PasswordEncoder passwordEncoder;

    // 회원가입
    public void signup(SignupRequest signupRequest) {
        log.info("signup signupRequest: {}", signupRequest);

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

    // 닉네임 중복확인
    @Transactional(readOnly = true)
    public boolean isNicknameDuplicated(String nickname) {
        return userRepository.existsByNickname(nickname);
    }

    // 이메일 중복확인
    @Transactional(readOnly = true)
    public boolean isEmailDuplicated(String email) {
        return userRepository.existsByEmail(email);
    }

    // 회원 정보 조회
    @Transactional(readOnly = true)
    public ProfileResponse getProfile(Long userId) {
        log.info("getProfile userId: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.UNAUTHORIZED));

        return userMapper.toProfileResponse(user);
    }

    // 회원 정보 수정 - 닉네임, 프로필 사진
    @Transactional
    public ProfileResponse editProfile(ProfileEditRequest request, Long userId) {
        log.info("editProfile profileEditRequest: {}", request);
        //사용자 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.UNAUTHORIZED));
        // 닉네임
        String newNickname = request.nickname();
        if (userRepository.existsByNicknameAndIdNot(newNickname, userId)) {
            throw new CustomException(ErrorCode.EXISTED_NICKNAME);
        }
        // 프로필 이미지
        ProfileImage newProfileImage = user.getProfileImage();
        if (request.profileImage() != null) {
            // 기존 프로필 이미지 논리 삭제
            ProfileImage oldProfileImage = user.getProfileImage();
            if (oldProfileImage != null && oldProfileImage.getDeletedAt() == null) {
                oldProfileImage.markAsDeleted();
            }

            // 새 프로필 이미지 생성 및 저장
            newProfileImage = profileImageMapper.toEntity(request.profileImage());
            newProfileImage = profileImageRepository.save(newProfileImage);
        }

        user.updateProfile(request.nickname(), newProfileImage);

        return userMapper.toProfileResponse(user);
    }

    // 비밀번호 수정
    public void editPassword(PasswordEditRequest request, Long userId) {
        log.info("editPassword userId: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.UNAUTHORIZED));

        String encodedPassword = passwordEncoder.encode(request.password());
        user.updatePassword(encodedPassword);
    }

}
