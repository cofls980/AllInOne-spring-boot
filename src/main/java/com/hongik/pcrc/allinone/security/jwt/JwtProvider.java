package com.hongik.pcrc.allinone.security.jwt;

import com.hongik.pcrc.allinone.exception.AllInOneException;
import com.hongik.pcrc.allinone.exception.MessageType;
import com.hongik.pcrc.allinone.security.service.CustomUserDetailService;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import java.security.Key;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@Component
public class JwtProvider {

    /*
    * 토큰을 생성하고 해당 토큰이 유효한지 또는 토큰에서 인증 정보를 조회하는 역할
    * */
    private final Key secretKey;
    private final Long accessExpiredTime;
    private final Long refreshExpiredTime;
    private final CustomUserDetailService customUerDetailService;

    public JwtProvider(@Value("${external.jwt.secretKey}") String secretKey,
                       @Value("${external.jwt.accessTokenExpiredTime}") Long accessExpiredTime,
                       @Value("${external.jwt.refreshTokenExpiredTime}") Long refreshExpiredTime,
                       CustomUserDetailService customUerDetailService) {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.secretKey = Keys.hmacShaKeyFor(keyBytes);
        this.accessExpiredTime = accessExpiredTime;
        this.refreshExpiredTime = refreshExpiredTime;
        this.customUerDetailService = customUerDetailService;
    }

    public TokenInfo generateToken(String email) {//Authentication authentication

        Map<String, Object> headers = new HashMap<>();
        headers.put("type", "token");

        Map<String, Object> payloads = new HashMap<>();
        payloads.put("email", email);

        Date now = new Date();

        //Access Token 생성
        String accessToken = Jwts.builder()
                .setHeader(headers)
                .setClaims(payloads)
                .setSubject("allinone")
                .setExpiration(new Date(now.getTime() + accessExpiredTime))
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();

        //Refresh Token 생성
        String refreshToken = Jwts.builder()
                .setExpiration(new Date(now.getTime() + refreshExpiredTime))
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();

        return TokenInfo.builder()
                .grantType("Bearer")
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    //Jwt 토큰을 복호화하여 토큰에 들어있는 정보를 꺼내는 메서드
    public Authentication getAuthentication(ServletRequest request, String accessToken) {

        UserDetails userDetails;
        try {
            userDetails = customUerDetailService.loadUserByUsername((String) parseClaims(accessToken).get("email"));//기본적으로 제공한 details 세팅
        } catch (UsernameNotFoundException ex) {
            request.setAttribute("exception", "UsernameOrPasswordNotFound");
            return null;
        }

        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    public JWTEnum validateJwtToken(ServletRequest request, String token) {

        try {
            Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token);
            return JWTEnum.VALID;
        } catch (SecurityException | MalformedJwtException exception) {
            request.setAttribute("exception", "MalformedJwtException"); // JWT가 올바르게 구성되지 않았을 때
        } catch (ExpiredJwtException exception) {
            request.setAttribute("exception", "ExpiredJwtException"); // 토큰 만료
        } catch (UnsupportedJwtException exception) {
            request.setAttribute("exception", "UnsupportedJwtException"); // 예상하는 형식과 일치하지 않는 특정 형식이나 구성의 JWT일 경우
        } catch (JwtException | IllegalArgumentException exception) {
            request.setAttribute("exception", "IllegalArgumentException");
        }

        return JWTEnum.INVLID;
    }

    public Claims parseClaims(String accessToken) {
        try {
            return Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(accessToken).getBody();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }

    //Request Header에서 토큰 정보 추출
    public String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

}
