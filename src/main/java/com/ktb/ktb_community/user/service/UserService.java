package com.ktb.ktb_community.user.service;

import com.ktb.ktb_community.global.exception.CustomException;
import com.ktb.ktb_community.global.exception.ErrorCode;
import com.ktb.ktb_community.user.dto.request.PasswordCheckRequest;
import com.ktb.ktb_community.user.dto.request.PasswordEditRequest;
import com.ktb.ktb_community.user.dto.request.ProfileEditRequest;
import com.ktb.ktb_community.user.dto.request.SignupRequest;
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
    public void signup(SignupRequest request) {
        log.info("signup signupRequest: {}", request);

        // 이메일 중복체크
        if(userRepository.existsByEmail(request.email())) {
            throw new CustomException(ErrorCode.EXISTED_EMAIL);
        }

        // 닉네임 중복 체크
        if(userRepository.existsByNickname(request.nickname())) {
            throw new CustomException(ErrorCode.EXISTED_NICKNAME);
        }

        // 프로필 이미지
        ProfileImage profileImage = null;
        if (request.profileImage() != null) {
            // 프로필 이미지 생성 및 저장
            profileImage = profileImageMapper.toEntity(request.profileImage());
            profileImageRepository.save(profileImage);
        }

        User user = userMapper.toEntity(request, profileImage);
        userRepository.save(user);
    }

    // 닉네임 중복확인 - 회원정보 수정
    @Transactional(readOnly = true)
    public boolean isNicknameDuplicated(String nickname, Long userId) {
        boolean result = userRepository.existsByNicknameAndIdNot(nickname,  userId);
        log.info("isNicknameDuplicated: {}", result);
        return result;
    }

    // 닉네임 중복확인 - 회원가입
    @Transactional(readOnly = true)
    public boolean isNicknameExisted(String nickname) {
        boolean result = userRepository.existsByNickname(nickname);
        log.info("isNicknameExisted: {}", result);
        return result;
    }

    // 이메일 중복확인 - 회원가입
    @Transactional(readOnly = true)
    public boolean isEmailExisted(String email) {
        boolean result = userRepository.existsByEmail(email);
        log.info("isEmailExisted: {}", result);
        return result;
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
        // 닉네임 중복확인
        String newNickname = request.nickname();
        if (userRepository.existsByNicknameAndIdNot(newNickname, userId)) {
            throw new CustomException(ErrorCode.EXISTED_NICKNAME);
        }
        // 프로필 이미지
        ProfileImage newProfileImage = user.getProfileImage();
        if (request.profileImage() != null) {
            // 기존 프로필 이미지 논리
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

    // 기존 비밀번호 확인
    public boolean checkPassword(PasswordCheckRequest request, Long userId) {
        log.info("checkPassword userId: {}", userId);
        // 유저 확인
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.UNAUTHORIZED));
        // 유저 기존 비밀번호 확인
        String originalPassword = user.getPassword();
        boolean result = passwordEncoder.matches(request.oldPassword(), originalPassword);

        return result;
    }

    // 비밀번호 수정
    public void editPassword(PasswordEditRequest request, Long userId) {
        log.info("editPassword userId: {}", userId);
        // 유저 확인
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.UNAUTHORIZED));
        // 유저 기존 비밀번호 확인
        String originalPassword = user.getPassword();
        boolean result = passwordEncoder.matches(request.oldPassword(), originalPassword);
        if (!result) {
            throw new CustomException(ErrorCode.PASSWORD_NOT_MATCH);
        }
        // 새 비밀번호화 확인 비밀번호가 같은지 확인
        if(!request.newPassword().equals(request.confirmPassword())){
            throw new CustomException(ErrorCode.PASSWORD_NOT_EQUAL);
        }
        // 새 비밀번호로 변경
        String encodedPassword = passwordEncoder.encode(request.newPassword());
        user.updatePassword(encodedPassword);
    }

    // 프로필 이미지 조회 - S3/CloudFront 사용으로 더 이상 필요 없음
    // Mapper에서 URL을 직접 반환하므로 이 메서드는 제거 예정

}
