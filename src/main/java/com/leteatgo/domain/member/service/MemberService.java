package com.leteatgo.domain.member.service;

import static com.leteatgo.global.exception.ErrorCode.NOT_FOUND_MEMBER;

import com.leteatgo.domain.member.dto.response.MyInfoResponse;
import com.leteatgo.domain.member.entity.Member;
import com.leteatgo.domain.member.exception.MemberException;
import com.leteatgo.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class MemberService {

    private final MemberRepository memberRepository;

    public MyInfoResponse myInformation(String authId) {
        Member member = memberRepository.findById(Long.parseLong(authId))
                .orElseThrow(() -> new MemberException(NOT_FOUND_MEMBER));

        return MyInfoResponse.fromEntity(member);
    }
}
