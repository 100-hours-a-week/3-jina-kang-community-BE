package com.ktb.ktb_community.user.mapper;

import com.ktb.ktb_community.user.dto.response.UserInfo;
import com.ktb.ktb_community.user.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserInfo toUserInfo(User user) {
        return new UserInfo(
                user.getId(),
                user.getRole(),
                user.getProfileImage().getUrl()
        );
    }
}
