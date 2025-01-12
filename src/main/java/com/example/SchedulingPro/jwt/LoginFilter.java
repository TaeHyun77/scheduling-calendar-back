package com.example.SchedulingPro.jwt;

import com.example.SchedulingPro.details.CustomOauth2UserDetails;
import com.example.SchedulingPro.entity.Refresh;
import com.example.SchedulingPro.repository.RefreshRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;

@RequiredArgsConstructor
@Slf4j
@Component
public class LoginFilter extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtUtil jwtUtil;
    private final RefreshRepository refreshRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        // 유저 정보 -> oauth 로그인 성공 시 받는 사용자 정보를 이용해서 jwt 토큰 발급 받음
        CustomOauth2UserDetails userDetails = (CustomOauth2UserDetails) authentication.getPrincipal();

        String username = userDetails.getUsername();
        String name = userDetails.getName();
        String email = userDetails.getEmail();

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
        GrantedAuthority auth = iterator.next();

        String role = auth.getAuthority();

        // 토큰 생성
        String accessToken = jwtUtil.createJwt("access", username, name, 3600000L); // 1시간
        String refreshToken = jwtUtil.createJwt("refresh", username, name, 86400000L); // 24시간

        log.info("accessToken: " + accessToken);
        log.info("refreshToken: " + refreshToken);

        addRefreshEntity(username, refreshToken, 60*60*60*10L);

        response.setHeader("Authorization", accessToken);

        // 쿠키에 jwt 토큰 담음
        response.addCookie(createCookie("accessToken", accessToken));
        response.addCookie(createCookie("refreshToken", refreshToken));
        response.setStatus(HttpStatus.OK.value());
        response.sendRedirect("http://localhost:3000/");
    }

    public void addRefreshEntity(String username, String refresh, Long expired) {

        Date date = new Date(System.currentTimeMillis() + expired);

        Refresh refreshEntity = Refresh.builder()
                .username(username)
                .refresh(refresh)
                .expiration(date.toString())
                .build();

        refreshRepository.save(refreshEntity);
    }

    private Cookie createCookie(String key, String value) {
        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(60*60*60);
        cookie.setPath("/");
        return cookie;
    }
}
