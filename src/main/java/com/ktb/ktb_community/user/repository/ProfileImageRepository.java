package com.ktb.ktb_community.user.repository;

import com.ktb.ktb_community.user.entity.ProfileImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProfileImageRepository extends JpaRepository<ProfileImage, Long> {
}
