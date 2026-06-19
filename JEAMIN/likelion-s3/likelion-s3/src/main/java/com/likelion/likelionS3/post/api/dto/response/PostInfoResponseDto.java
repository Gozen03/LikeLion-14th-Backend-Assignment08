package com.likelion.likelionS3.post.api.dto.response;

import com.likelion.likelionS3.post.domain.Post;
import lombok.Builder;

@Builder
public record PostInfoResponseDto(
        String title,
        String contents,
        String writer,
        String imageUrl // S3에 업로드된 이미지 URL, 이미지 없으면 null 반환
) {
    public static PostInfoResponseDto from(Post post) {
        return PostInfoResponseDto.builder()
                .title(post.getTitle())
                .contents(post.getContents())
                .writer(post.getMember().getName())
                .imageUrl(post.getImageUrl()) // 이미지 없이 저장된 게시글이면 null
                .build();
    }
}
