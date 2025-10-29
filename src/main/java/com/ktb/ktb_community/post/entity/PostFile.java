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

    @Column(name = "file_name", nullable = false)
    private String fileName;        // 원본 파일 이름

    @Column(nullable = false)
    private String url;             // 저장된 파일명

    @Column(name = "image_index", nullable = false)
    private int imageIndex = 1;     // 파일 저장 순서

    @Column(nullable = false)
    private String contentType;            // MIME

    @CreatedDate
    @Column(name = "created_at", nullable = false,  updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    public void changeToDeleted() {
        this.deletedAt = LocalDateTime.now();
    }

    public void updateIndex(int index){
        this.imageIndex = index;
    }
}
