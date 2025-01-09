package com.ll.webchattingserver.global.security.jwt;

import com.ll.webchattingserver.global.exception.clazz.security.InvalidTokenAccessException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        // 인증이 필요없는 엔드포인트들은 필터 검증에서 제외
        if (request.getRequestURI().equals("/api/auth/refresh") ||
                request.getRequestURI().equals("/api/auth/login") ||
                request.getRequestURI().equals("/api/auth/signup") ||
                request.getRequestURI().equals("/ws-stomp")) {  // WebSocket 엔드포인트 추가) {  // 로그인 API 추가
            filterChain.doFilter(request, response);
            return;
        }

        String token = resolveToken(request);
        if (token == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        try {
            jwtProvider.validateToken(token);
            Authentication auth = jwtProvider.getAuthentication(token);
            SecurityContextHolder.getContext().setAuthentication(auth);
            filterChain.doFilter(request, response);

        } catch(InvalidTokenAccessException e){
            // 토큰이 만료되었거나 유효하지 않은 경우
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        }catch(Exception e){

            // 토큰 처리 중 에러가 발생한 경우
            log.error("JWT Token processing error: ", e);
            SecurityContextHolder.clearContext();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        return jwtProvider.extractToken(bearerToken);
    }
}
