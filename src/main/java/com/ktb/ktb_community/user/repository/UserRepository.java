package com.ktb.ktb_community.user.repository;

import com.ktb.ktb_community.user.entity.User;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository {

    Optional<User> findByEmailAndDeletedAtIsNull(String email);
}
