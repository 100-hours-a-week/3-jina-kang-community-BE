package com.ktb.ktb_community.user.service;

import com.ktb.ktb_community.user.dto.request.SignupRequest;
import com.ktb.ktb_community.user.entity.ProfileImage;
import com.ktb.ktb_community.user.entity.User;
import com.ktb.ktb_community.user.entity.UserRole;
import com.ktb.ktb_community.user.mapper.UserMapper;
import com.ktb.ktb_community.user.repository.ProfileImageRepository;
import com.ktb.ktb_community.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    ProfileImageRepository profileImageRepository;

    @Mock
    UserMapper userMapper;

    @InjectMocks
    private UserService userService;

    @Test
    public void createPost() {
        // given
        LocalDateTime testTime = LocalDateTime.of(2025, 10, 21, 14, 30);
        SignupRequest request = new SignupRequest("test@test.com", "testPassword", "testNickname", 1L);
        ProfileImage profileImage = ProfileImage.builder()
                .id(1L)
                .url("https://example.com/profile.jpg")
                .createdAt(testTime)
                .build();
        User user = User.builder()
                .id(null)
                .email(request.email())
                .password(request.password())
                .nickname(request.nickname())
                .role(UserRole.USER)
                .profileImage(profileImage)
                .build();
        User savedUser = User.builder()
                .id(1L)
                .email(request.email())
                .password(request.password())
                .nickname(request.nickname())
                .role(UserRole.USER)
                .profileImage(profileImage)
                .createdAt(testTime)
                .build();

        when(userRepository.existsByEmail(request.email())).thenReturn(false);
        when(userRepository.existsByNickname(request.nickname())).thenReturn(false);
        when(profileImageRepository.findById(request.profileImageId())).thenReturn(Optional.of(profileImage));
        when(userMapper.toEntity(request, profileImage)).thenReturn(user);
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        // When
        userService.signup(request);

        // Then
        verify(userRepository).existsByEmail(request.email());
        verify(userRepository).existsByNickname(request.nickname());
        verify(profileImageRepository).findById(request.profileImageId());
        verify(userMapper).toEntity(request, profileImage);
        verify(userRepository).save(any(User.class));
    }
}
