package com.hongik.pcrc.allinone.security.jwt;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends GenericFilterBean {

    private final JwtProvider jwtProvider;

    /*
    * Jwt 토큰의 인증 정보를 현재 실행 중인 SecurityContext에 저장하는 역할을 수행
    * request의 header에서 토큰을 가져오고 유효성 체크 후 해당 토큰이 유효하다면 Security Context에 인증 정보 저장
    * */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        // 헤더에서 JWT 받아온다.
        String token = jwtProvider.resolveToken((HttpServletRequest) request);

        // 유효한 토큰인지 확인한다.
        if (token != null && jwtProvider.validateJwtToken(request, token) == JWTEnum.VALID) {
            // 토큰이 유효하면 토큰으로부터 유저 정보 받아온다.
            Authentication authentication = jwtProvider.getAuthentication(request, token);
            // SecurityContext에 Authentication 객체를 저장한다.
            SecurityContextHolder.getContext().setAuthentication(authentication);//세션에서 계속 사용하기 위해 securityContext에 Authentication 등록
        }
        chain.doFilter(request, response);
    }
}
