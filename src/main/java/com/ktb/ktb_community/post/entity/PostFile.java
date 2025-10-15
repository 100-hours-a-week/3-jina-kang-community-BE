package com.ktb.ktb_community.post.entity;

import com.ktb.ktb_community.global.file.entity.FileType;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
@Table(name = "post_file", indexes = {
        @Index(name = "idx_post_file_post_id", columnList = "post_id")
})
public class PostFile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id",  nullable = false)
    private Post post;

    @Column(nullable = false)
    private String url;

    @Column(name = "image_index", nullable = false)
    private int ImageIndex = 1;

    @Column(nullable = false)
    private FileType type;

    @CreatedDate
    @Column(name = "created_at", nullable = false,  updatable = false)
    private LocalDateTime createdAt;
}
