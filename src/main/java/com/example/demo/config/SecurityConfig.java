package com.example.demo.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/register", "/login", "/css/**", "/js/**", "/images/**", "/courses/**",
                                "/board/paging/**", "/board/{id}/**")
                        .permitAll()
                        .anyRequest().authenticated()
                )
                .formLogin(login -> login
                        .loginPage("/login") // 로그인 페이지 직접 지정
                        .loginProcessingUrl("/login") // 이 경로로 POST 요청시 인증 처리
                        .usernameParameter("email")   // 로그인 폼에서 name="email"
                        .successHandler((request, response, authentication) -> {
                            String redirect = request.getParameter("redirect");
                            if (redirect != null && !redirect.isEmpty()) {
                                // redirect가 /로 시작되면 그대로 보냄 (ex. /board/save)
                                if (redirect.startsWith("/")) {
                                    response.sendRedirect(redirect);
                                } else {
                                    response.sendRedirect("/courses/" + redirect);
                                }
                            } else {
                                response.sendRedirect("/courses");
                            }
                        })
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutSuccessHandler((request, response, authentication) -> {
                            String redirect = request.getParameter("redirect");
                            if (redirect != null && !redirect.isEmpty()) {
                                response.sendRedirect(redirect);
                            } else {
                                response.sendRedirect("/courses");
                            }
                        })
                        .permitAll()
                );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
