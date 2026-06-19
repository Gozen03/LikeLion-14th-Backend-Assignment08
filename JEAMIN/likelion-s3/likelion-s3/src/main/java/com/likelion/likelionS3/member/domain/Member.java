package com.likelion.likelionS3.member.domain;

import com.likelion.likelionS3.member.api.dto.request.MemberUpdateRequestDto;
import com.likelion.likelionS3.post.domain.Post;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long memberId;

    private String name;

    private int age;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private Part part;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Post> posts = new ArrayList<>();

    @Builder
    private Member(String name, int age, Part part) {
        this.name = name;
        this.age = age;
        this.part = part;
    }

    public void update(MemberUpdateRequestDto memberUpdateRequestDto) {
        this.name = memberUpdateRequestDto.name();
        this.age = memberUpdateRequestDto.age();
    }
}
