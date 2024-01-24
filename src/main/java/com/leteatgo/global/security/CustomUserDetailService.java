package com.leteatgo.global.security;

import static com.leteatgo.global.exception.ErrorCode.NOT_FOUND_MEMBER;

import com.leteatgo.domain.member.exception.MemberException;
import com.leteatgo.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CustomUserDetailService implements UserDetailsService {

    private final MemberRepository memberRepository;

    @Override
    @Transactional(readOnly = true)
    public CustomUserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return memberRepository.findByEmail(email)
                .map(CustomUserDetails::new)
                .orElseThrow(() -> new MemberException(NOT_FOUND_MEMBER));
    }

    @Transactional(readOnly = true)
    public CustomUserDetails loadUserById(Long memberId) throws UsernameNotFoundException {
        return memberRepository.findById(memberId)
                .map(CustomUserDetails::new)
                .orElseThrow(() -> new MemberException(NOT_FOUND_MEMBER));
    }

}
