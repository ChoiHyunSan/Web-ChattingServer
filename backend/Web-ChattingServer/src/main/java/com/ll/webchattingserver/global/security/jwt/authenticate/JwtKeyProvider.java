package com.ll.webchattingserver.global.security.jwt.authenticate;

import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;

@Component
public class JwtKeyProvider {

    @Value("${token.secret-key}")
    private String secretKeyStr;

    @Value("${token.refresh-secret-key}")
    private String refreshSecretKeyStr;

    @Getter
    private Key accessKey;

    @Getter
    private Key refreshKey;

    @PostConstruct
    public void init() {
        this.accessKey = Keys.hmacShaKeyFor(secretKeyStr.getBytes());
        this.refreshKey = Keys.hmacShaKeyFor(refreshSecretKeyStr.getBytes());
    }

}
