package com.ll.webchattingserver.global.config.admin;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "admin")
@Getter
@Setter
public class AdminConfig {
    private String username;
    private String password;
    private String email;
}