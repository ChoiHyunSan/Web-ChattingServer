package com.ll.webchattingserver.global.config.admin;

import com.ll.webchattingserver.domain.user.User;
import com.ll.webchattingserver.domain.user.UserRole;
import com.ll.webchattingserver.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;


@Configuration
@RequiredArgsConstructor
public class InitialDataConfig {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    @Value("${admin.username}")
    private String adminUsername;

    @Value("${admin.password}")
    private String adminPassword;

    @Value("${admin.email}")
    private String adminEmail;

    @Bean
    public ApplicationRunner init() {
        return args -> {
            // 이미 admin 계정이 있는지 확인
            if (!userService.existsByUsername("admin")) {
                String password = passwordEncoder.encode(adminPassword);
                User admin = User.builder()
                        .username(adminUsername)
                        .password(password)
                        .email(adminEmail)
                        .role(UserRole.ROLE_ADMIN)
                        .build();

                userService.save(admin);
            }
        };
    }
}
