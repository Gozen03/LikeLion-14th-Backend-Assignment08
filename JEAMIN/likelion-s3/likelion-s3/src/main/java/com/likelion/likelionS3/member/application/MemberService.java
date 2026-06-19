package com.likelion.likelionS3.member.application;


import com.likelion.likelionS3.common.exception.BusinessException;
import com.likelion.likelionS3.common.response.code.ErrorCode;
import com.likelion.likelionS3.member.api.dto.request.MemberSaveRequestDto;
import com.likelion.likelionS3.member.api.dto.request.MemberUpdateRequestDto;
import com.likelion.likelionS3.member.api.dto.response.MemberInfoResponseDto;
import com.likelion.likelionS3.member.domain.Member;
import com.likelion.likelionS3.member.domain.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private final MemberRepository memberRepository;

    // 사용자 정보 저장
    @Transactional
    public void memberSave(MemberSaveRequestDto memberSaveRequestDto) {
        Member member = Member.builder()
                .name(memberSaveRequestDto.name())
                .age(memberSaveRequestDto.age())
                .part(memberSaveRequestDto.part())
                .build();
        memberRepository.save(member);
    }

    // 사용자 모두 조회
    public Page<MemberInfoResponseDto> memberFindAll(Pageable pageable) {
        Page<Member> members = memberRepository.findAll(pageable);
        return members.map(MemberInfoResponseDto::from);
    }

    // 단일 사용자 조회
    public MemberInfoResponseDto memberFindOne(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BusinessException(
                        ErrorCode.MEMBER_NOT_FOUND_EXCEPTION,
                        ErrorCode.MEMBER_NOT_FOUND_EXCEPTION.getMessage() + memberId
                ));

        return MemberInfoResponseDto.from(member);
    }

    // 사용자 정보 수정
    @Transactional
    public void memberUpdate(Long memberId, MemberUpdateRequestDto memberUpdateRequestDto) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BusinessException(
                        ErrorCode.MEMBER_NOT_FOUND_EXCEPTION,
                        ErrorCode.MEMBER_NOT_FOUND_EXCEPTION.getMessage() + memberId));
        member.update(memberUpdateRequestDto);
    }

    // 사용자 정보 삭제
    @Transactional
    public void memberDelete(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BusinessException(
                        ErrorCode.MEMBER_NOT_FOUND_EXCEPTION,
                        ErrorCode.MEMBER_NOT_FOUND_EXCEPTION.getMessage() + memberId));
        memberRepository.delete(member);
    }
}
