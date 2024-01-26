package com.leteatgo.global.security.oauth.service;

import com.leteatgo.domain.member.entity.Member;
import com.leteatgo.domain.member.repository.MemberRepository;
import com.leteatgo.global.security.CustomOAuth2UserDetails;
import com.leteatgo.global.security.oauth.dto.OAuth2UserInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final MemberRepository memberRepository;

    @Transactional
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        String providerName = userRequest.getClientRegistration().getRegistrationId();

        OAuth2UserInfo oAuth2UserInfo = extractUserInfoFromOAuth2User(oAuth2User, providerName);
        Member member = getOrRegister(oAuth2UserInfo);

        return new CustomOAuth2UserDetails(member, oAuth2User.getAttributes());
    }

    private OAuth2UserInfo extractUserInfoFromOAuth2User(OAuth2User oAuth2User,
            String providerName) {
        return OAuth2Provider
                .getOAuthProviderByName(providerName)
                .toUserInfo(oAuth2User);
    }

    private Member getOrRegister(OAuth2UserInfo oAuth2UserInfo) {
        return memberRepository.findByEmail(oAuth2UserInfo.email())
                .orElseGet(() -> memberRepository.save(oAuth2UserInfo.toEntity()));
    }

}
