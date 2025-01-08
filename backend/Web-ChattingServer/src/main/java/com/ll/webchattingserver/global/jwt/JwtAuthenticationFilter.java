package com.ll.webchattingserver.global.jwt;

import com.ll.webchattingserver.global.exception.InvalidTokenAccessException;
import com.ll.webchattingserver.global.security.UserPrincipal;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        // 인증이 필요없는 엔드포인트들은 필터 검증에서 제외
        if (request.getRequestURI().equals("/api/auth/refresh") ||
                request.getRequestURI().equals("/api/auth/login") ||
                request.getRequestURI().equals("/ws-stomp")) {  // WebSocket 엔드포인트 추가) {  // 로그인 API 추가
            filterChain.doFilter(request, response);
            return;
        }

        String token = resolveToken(request);
        log.debug("Received token: {}", token);

        if (token == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \"토큰이 없습니다.\"}");
            return;
        }

        try {
            jwtProvider.validateToken(token);
            Authentication auth = getRefreshAuthentication(token);
            SecurityContextHolder.getContext().setAuthentication(auth);
            filterChain.doFilter(request, response);
        } catch(InvalidTokenAccessException e){
            // 토큰이 만료되었거나 유효하지 않은 경우
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \"토큰이 만료되었습니다.\"}");
        }catch(Exception e){
            // 토큰 처리 중 에러가 발생한 경우
            log.error("JWT Token processing error: ", e);
            SecurityContextHolder.clearContext();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \"토큰 처리 중 오류가 발생했습니다.\"}");
        }
    }

    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    public Authentication getRefreshAuthentication(String token) {
        UserPrincipal principal = jwtProvider.getUserPrincipal(token);

        return new UsernamePasswordAuthenticationToken(
                principal,
                null,
                Collections.emptyList()
        );
    }
}
