package com.example.SchedulingPro.details;

import com.example.SchedulingPro.user.User;
import com.example.SchedulingPro.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;
import java.util.Map;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomOauth2UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CustomOauth2UserService customOauth2UserService;

    @Test
    void loadUser_NewUser_ShouldSaveUser() {
        // Mock 데이터
        OAuth2UserRequest userRequest = mock(OAuth2UserRequest.class);
        OAuth2User oAuth2User = mock(OAuth2User.class);

        when(userRequest.getClientRegistration().getRegistrationId()).thenReturn("kakao");
        when(oAuth2User.getAttributes()).thenReturn(Map.of(
                "id", "12345",
                "kakao_account", Map.of("email", "test@kakao.com"),
                "properties", Map.of("nickname", "Test User")
        ));

        // Mock 저장소의 동작
        when(userRepository.findByUsername(anyString())).thenReturn(null); // 기존 사용자 없음

        // 실행
        OAuth2User result = customOauth2UserService.loadUser(userRequest);

        // 검증
        verify(userRepository, times(1)).save(any(User.class));
        assertThat(((CustomOauth2UserDetails) result).getUser().getName()).isEqualTo("Test User");
    }

    @Test
    void loadUser_ExistingUser_ShouldNotSaveUser() {
        // Mock 데이터
        OAuth2UserRequest userRequest = mock(OAuth2UserRequest.class);
        OAuth2User oAuth2User = mock(OAuth2User.class);

        when(userRequest.getClientRegistration().getRegistrationId()).thenReturn("kakao");
        when(oAuth2User.getAttributes()).thenReturn(Map.of(
                "id", "12345",
                "kakao_account", Map.of("email", "test@kakao.com"),
                "properties", Map.of("nickname", "Test User")
        ));

        User existingUser = new User("kakao_12345", "Test User", "ROLE_USER", "kakao", "12345");

        when(userRepository.findByUsername(anyString())).thenReturn(existingUser);

        // 실행
        OAuth2User result = customOauth2UserService.loadUser(userRequest);

        // 검증
        verify(userRepository, never()).save(any(User.class)); // 저장 호출 안 됨
        assertThat(((CustomOauth2UserDetails) result).getUser().getName()).isEqualTo("Test User");
    }
}
