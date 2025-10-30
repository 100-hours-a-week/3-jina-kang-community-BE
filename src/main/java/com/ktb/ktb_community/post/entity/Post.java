package com.ktb.ktb_community.post.entity;

import com.ktb.ktb_community.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
@Table(name = "post", indexes = {
        @Index(name = "idx_post_deleted_at", columnList = "deleted_at")
})
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 255)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @OneToOne(fetch = FetchType.LAZY, mappedBy = "post",  cascade = CascadeType.ALL)
    private PostStatus postStatus;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    public void updatePost(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public void delete() {
        this.deletedAt = LocalDateTime.now();
    }

    public void initPostStatus() {
        if(this.postStatus == null) {
            this.postStatus = PostStatus.builder()
                    .post(this)
                    .likeCount(0L)
                    .viewCount(0L)
                    .build();
        }
    }
}
