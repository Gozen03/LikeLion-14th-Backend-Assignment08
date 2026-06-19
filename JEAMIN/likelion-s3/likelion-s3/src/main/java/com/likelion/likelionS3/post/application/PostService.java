package com.likelion.likelionS3.post.application;

import com.likelion.likelionS3.common.exception.BusinessException;
import com.likelion.likelionS3.common.response.code.ErrorCode;
import com.likelion.likelionS3.image.S3Uploader;
import com.likelion.likelionS3.member.domain.Member;
import com.likelion.likelionS3.member.domain.repository.MemberRepository;
import com.likelion.likelionS3.post.api.dto.request.PostSaveRequestDto;
import com.likelion.likelionS3.post.api.dto.request.PostUpdateRequestDto;
import com.likelion.likelionS3.post.api.dto.response.PostInfoResponseDto;
import com.likelion.likelionS3.post.domain.Post;
import com.likelion.likelionS3.post.domain.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostService {

    private final MemberRepository memberRepository;
    private final PostRepository postRepository;
    private final S3Uploader s3Uploader;

    // 게시물 저장
    @Transactional
    public void postSave(PostSaveRequestDto postSaveRequestDto, MultipartFile image) {
        Member member = memberRepository.findById(postSaveRequestDto.memberId()).orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND_EXCEPTION, ErrorCode.MEMBER_NOT_FOUND_EXCEPTION.getMessage() + postSaveRequestDto.memberId()));

        // 이미지가 있을 때만 S3에 업로드. 이미지 없이 글만 작성하는 경우도 허용하기 위해 null 체크
        String imageUrl = null;
        if (image != null && !image.isEmpty()) {
            try {
                imageUrl = s3Uploader.upload(image); // S3 업로드 후 반환된 URL 저장
            } catch (IOException e) {
                // 업로드 실패 시  커스텀 예외로 변환해서 던짐 -> GlobalExceptionHander가 처리
                throw new BusinessException(ErrorCode.FILE_UPLOAD_FAIL_EXCEPTION, ErrorCode.FILE_UPLOAD_FAIL_EXCEPTION.getMessage());
            }
        }

        Post post = Post.builder()
                .title(postSaveRequestDto.title())
                .contents(postSaveRequestDto.contents())
                .imageUrl(imageUrl) // 이미지 없으면 null
                .member(member)
                .build();

        postRepository.save(post);
    }

    // 특정 작성자가 작성한 게시글 목록을 조회
    public Page<PostInfoResponseDto> postFindMember(Long memberId, Pageable pageable) {
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND_EXCEPTION, ErrorCode.MEMBER_NOT_FOUND_EXCEPTION.getMessage() + memberId));

        Page<Post> posts = postRepository.findByMember(member, pageable);
        return posts.map(PostInfoResponseDto::from);
    }

    // 게시물 수정
    @Transactional
    public void postUpdate(Long postId, PostUpdateRequestDto postUpdateRequestDto, MultipartFile image)
    {
        Post post = postRepository.findById(postId).orElseThrow(() ->
                new BusinessException(ErrorCode.POST_NOT_FOUND_EXCEPTION, ErrorCode.POST_NOT_FOUND_EXCEPTION.getMessage() + postId));

        // 기존 이미지 URL을 기본값으로 설정 (새 이미지가 안 들어오면 기존 URL 유지)
        String finalImageUrl = post.getImageUrl();

        // 1. 새로운 이미지가 전달된 경우
        if (image != null && !image.isEmpty()) {

            // 1-1. 기존에 저장된 이미지가 있었다면 S3 버킷에서 삭제
            if (finalImageUrl != null) {
                s3Uploader.delete(finalImageUrl);
            }

            // 1-2. 새로운 이미지를 S3 버킷에 업로드하고 새 URL 받아오기
            try {
                finalImageUrl = s3Uploader.upload(image);
            } catch (IOException e) {
                throw new BusinessException(ErrorCode.FILE_UPLOAD_FAIL_EXCEPTION, ErrorCode.FILE_UPLOAD_FAIL_EXCEPTION.getMessage());
            }
        }

        // 2. 게시글 정보 업데이트 (엔티티의 update 메서드 호출)
        post.update(postUpdateRequestDto, finalImageUrl);
    }

    // 게시물 삭제
    @Transactional
    public void postDelete(Long postId) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new BusinessException(ErrorCode.POST_NOT_FOUND_EXCEPTION, ErrorCode.POST_NOT_FOUND_EXCEPTION.getMessage() + postId));
        postRepository.delete(post);
    }
}
