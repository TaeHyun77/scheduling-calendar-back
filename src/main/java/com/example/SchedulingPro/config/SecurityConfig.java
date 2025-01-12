package com.example.SchedulingPro.config;

import com.example.SchedulingPro.details.CustomOauth2UserService;
import com.example.SchedulingPro.jwt.CustomAuthenticationFailureHandler;
import com.example.SchedulingPro.jwt.JwtFilter;
import com.example.SchedulingPro.jwt.JwtUtil;
import com.example.SchedulingPro.jwt.LoginFilter;
import com.example.SchedulingPro.repository.RefreshRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Collections;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomOauth2UserService customOauth2UserService;
    private final AuthenticationConfiguration authenticationConfiguration; // 인증 관리 설정을 제공하는 객체
    private final JwtUtil jwtUtil;
    private final LoginFilter loginFilter;
    private final CustomAuthenticationFailureHandler customAuthenticationFailureHandler;

    private RefreshRepository refreshRepository;

    @Bean
    public AuthenticationManager authenticationManager (AuthenticationConfiguration configuration) throws Exception{
        return configuration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{

        // 접근 권한 설정
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                .csrf((auth) -> auth.disable())

                .formLogin(form -> form.disable())

                .httpBasic((auth) -> auth.disable())

                .addFilterBefore(new JwtFilter(jwtUtil), UsernamePasswordAuthenticationFilter.class)

                .oauth2Login((oauth2) -> oauth2
                        .userInfoEndpoint((userInfoEndpointConfig) -> userInfoEndpointConfig
                                .userService(customOauth2UserService))
                        .successHandler(loginFilter) // 로그인 성공 시
                        .failureHandler(customAuthenticationFailureHandler) // 로그인 실패 시
                )

                .authorizeHttpRequests((auth) -> auth
                        .requestMatchers("/", "/reissue", "/api").permitAll()
                        .anyRequest().permitAll()
                )


                .sessionManagement((session) -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS));


        return http.build();
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() { // Security Filter Chain 내에서 CORS 정책을 적용

        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOrigins(Collections.singletonList("http://localhost:3000"));
        configuration.setAllowedMethods(Collections.singletonList("*"));
        configuration.setAllowCredentials(true);
        configuration.setAllowedHeaders(Collections.singletonList("*"));
        configuration.setExposedHeaders(Collections.singletonList("Authorization"));
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

}