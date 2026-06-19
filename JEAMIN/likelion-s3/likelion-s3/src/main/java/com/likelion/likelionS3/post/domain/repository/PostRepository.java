package com.likelion.likelionS3.post.domain.repository;

import com.likelion.likelionS3.member.domain.Member;
import com.likelion.likelionS3.post.domain.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {
    Page<Post> findByMember(Member member, Pageable pageable);
}
