package com.likelion.likelionS3.post.domain;

import com.likelion.likelionS3.member.domain.Member;
import com.likelion.likelionS3.post.api.dto.request.PostUpdateRequestDto;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id")
    private Long postId;

    private String title;

    private String contents;

    // S3에 업로드된 이미지의 URL을 저장, 이미가 없을 경우 null 허용
    private String imageUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Builder
    private Post(String title, String contents, String imageUrl, Member member) {
        this.title = title;
        this.contents = contents;
        this.imageUrl = imageUrl; // 이미지 없이 글만 작성할 경우 null로 저장됨
        this.member = member;
    }

    public void update(PostUpdateRequestDto postUpdateRequestDto, String imageUrl) {
        this.title = postUpdateRequestDto.title();
        this.contents = postUpdateRequestDto.contents();
        this.imageUrl = imageUrl;
    }
}
