package com.ktb.ktb_community.user.service;

import com.ktb.ktb_community.global.exception.CustomException;
import com.ktb.ktb_community.global.exception.ErrorCode;
import com.ktb.ktb_community.global.file.service.FileService;
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
import org.springframework.core.io.Resource;
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
    private final FileService fileService;

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

    // 닉네임 중복확인 - 회원정보 수정
    @Transactional(readOnly = true)
    public boolean isNicknameDuplicated(String nickname, Long userId) {
        boolean result = !userRepository.existsByNicknameAndIdNot(nickname,  userId);
        log.info("isNicknameDuplicated: {}", result);
        return result;
    }

    // 닉네임 중복확인 - 회원가입
    @Transactional(readOnly = true)
    public boolean isNicknameExisted(String nickname) {
        boolean result = !userRepository.existsByNickname(nickname);
        log.info("isNicknameExisted: {}", result);
        return result;
    }

    // 이메일 중복확인 - 회원가입
    @Transactional(readOnly = true)
    public boolean isEmailExisted(String email) {
        boolean result = !userRepository.existsByEmail(email);
        log.info("isEmailExisted: {}", result);
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
        // 닉네임 중복확인
        log.info("닉네임");
        String newNickname = request.nickname();
        if (userRepository.existsByNicknameAndIdNot(newNickname, userId)) {
            throw new CustomException(ErrorCode.EXISTED_NICKNAME);
        }
        // 프로필 이미지
        log.info("프사");
        ProfileImage newProfileImage = user.getProfileImage();
        if (request.profileImage() != null) {
            // 기존 프로필 이미지 논리
            log.info("프사삭제");
            ProfileImage oldProfileImage = user.getProfileImage();
            if (oldProfileImage != null && oldProfileImage.getDeletedAt() == null) {
                oldProfileImage.markAsDeleted();
            }

            // 새 프로필 이미지 생성 및 저장
            log.info("프사 저장");
            newProfileImage = profileImageMapper.toEntity(request.profileImage());
            newProfileImage = profileImageRepository.save(newProfileImage);
        }

        log.info("유저저장");
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

    // 프로필 이미지 조회
    @Transactional(readOnly = true)
    public Resource getProfileImage(String fileName, String token) {
        log.info("getProfileImage - fileName: {}", fileName);

        // 파일 존재 여부 확인
        ProfileImage profileImage = profileImageRepository.findByUrlAndDeletedAtIsNull(fileName)
                .orElseThrow(() -> new CustomException(ErrorCode.FILE_NOT_FOUND));

        // 실제 파일 조회
        return fileService.getFileWithToken(fileName, token);
    }

    // 프로필 이미지 ContentType 조회
    @Transactional(readOnly = true)
    public String getProfileImageContentType(String fileName) {
        ProfileImage profileImage = profileImageRepository.findByUrlAndDeletedAtIsNull(fileName)
                .orElseThrow(() -> new CustomException(ErrorCode.FILE_NOT_FOUND));

        String contentType = profileImage.getContentType();
        if (contentType == null || contentType.isEmpty()) {
            throw new CustomException(ErrorCode.INVALID_FILE);
        }

        return contentType;
    }

}
