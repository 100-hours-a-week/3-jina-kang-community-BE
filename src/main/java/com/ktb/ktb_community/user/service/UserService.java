package com.ktb.ktb_community.user.service;

import com.ktb.ktb_community.global.exception.CustomException;
import com.ktb.ktb_community.global.exception.ErrorCode;
import com.ktb.ktb_community.user.dto.request.EmailDuplicationRequest;
import com.ktb.ktb_community.user.dto.request.PasswordEditRequest;
import com.ktb.ktb_community.user.dto.request.ProfileEditRequest;
import com.ktb.ktb_community.user.dto.request.SignupRequest;
import com.ktb.ktb_community.user.dto.response.DuplicationResponse;
import com.ktb.ktb_community.user.dto.response.ProfileEditResponse;
import com.ktb.ktb_community.user.entity.ProfileImage;
import com.ktb.ktb_community.user.entity.User;
import com.ktb.ktb_community.user.mapper.UserMapper;
import com.ktb.ktb_community.user.repository.ProfileImageRepository;
import com.ktb.ktb_community.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final ProfileImageRepository profileImageRepository;
    private final UserMapper userMapper;
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
    public DuplicationResponse nicknameDuplication(ProfileEditRequest profileEditRequest) {
        DuplicationResponse response;
        if(userRepository.existsByNickname(profileEditRequest.nickname())) {
            response = new DuplicationResponse(true);
        }
        else {
            response = new DuplicationResponse(false);
        }

        return response;
    }
    // 이메일 중복확인
    public DuplicationResponse emailDuplication(EmailDuplicationRequest emailDuplicationRequest) {
        DuplicationResponse response;
        if(userRepository.existsByEmail(emailDuplicationRequest.email())) {
            response = new DuplicationResponse(true);
        }
        else {
            response = new DuplicationResponse(false);
        }

        return response;
    }

    // 회원 정보 수정 - 닉네임, 프로필 사진
    public ProfileEditResponse editProfile(ProfileEditRequest profileEditRequest, Long userId) {
        log.info("editProfile profileEditRequest: {}", profileEditRequest);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.UNAUTHORIZED));
        if(user.getNickname().equals(profileEditRequest.nickname())) {
            throw new CustomException(ErrorCode.BEFORE_NICKNAME);
        }

        if(userRepository.existsByNickname(profileEditRequest.nickname())) {
            throw new CustomException(ErrorCode.EXISTED_NICKNAME);
        }

        ProfileImage profileImage = profileImageRepository.findById(profileEditRequest.profileImageId())
                .orElseThrow(() -> new CustomException(ErrorCode.PROFILE_IMAGE_NOT_FOUND));

        user.updateProfile(profileEditRequest.nickname(), profileImage);

        return userMapper.toProfileEditResponse(user);
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
