package com.example.SchedulingPro.details;

import com.example.SchedulingPro.user.User;
import com.example.SchedulingPro.repository.UserRepository;
import com.example.SchedulingPro.user.UserRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class CustomOauth2UserService extends DefaultOAuth2UserService {

    // 첫 로그인이면 자동으로 회원가입 진행

    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        OAuth2User oAuth2User = super.loadUser(userRequest); // 인증 서버로부터 사용자의 정보를 가져옴
        log.info("getAttributes : {}",oAuth2User.getAttributes());

        String category = userRequest.getClientRegistration().getRegistrationId();

        OAuth2UserInfo oAuth2UserInfo = null;

        // 구글은 따로 분류
        if (category.equals("google")){
            log.info("구글 로그인");
            oAuth2UserInfo = new GoogleUserDetails(oAuth2User.getAttributes());
        } else if (category.equals("kakao")) {
            log.info("카카오 로그인");
            oAuth2UserInfo = new KakaoUserDetails(oAuth2User.getAttributes());
        } else if (category.equals("naver")) {
            log.info("네이버 로그인");
            oAuth2UserInfo = new NaverUserDetails(oAuth2User.getAttributes());
        }

        String categoryId = oAuth2UserInfo.getCategoryId();
        String email = oAuth2UserInfo.getEmail();
        String username = category + "_" + categoryId;
        String name = oAuth2UserInfo.getName();
        log.info("name, email : " + name + " , " + email );

        User findUser = userRepository.findByUsername(username);

        User user;

        if (findUser == null) {
            user = UserRequestDto.builder()
                    .username(username)
                    .name(name)
                    .category(category)
                    .categoryId(categoryId)
                    .role("ROLE_USER")
                    .build().toEntity();

            userRepository.save(user);
        } else {
            log.info("이미 가입하였습니다.");
            user = findUser;
        }
        return new CustomOauth2UserDetails(user, oAuth2User.getAttributes());
    }
}
