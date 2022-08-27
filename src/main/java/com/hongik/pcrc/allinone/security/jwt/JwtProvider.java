package com.hongik.pcrc.allinone.security.jwt;

import com.hongik.pcrc.allinone.security.service.CustomUserDetailService;
import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
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
    private final String secretKey;
    private final Long accessExpiredTime;
    private final Long refreshExpiredTime;
    private final CustomUserDetailService customUerDetailService;

    public JwtProvider(@Value("${external.jwt.secretKey}") String secretKey,
                       @Value("${external.jwt.accessTokenExpiredTime}") Long accessExpiredTime,
                       @Value("${external.jwt.refreshTokenExpiredTime}") Long refreshExpiredTime,
                       CustomUserDetailService customUerDetailService) {
        this.secretKey = secretKey;
        this.accessExpiredTime = accessExpiredTime;
        this.refreshExpiredTime = refreshExpiredTime;
        this.customUerDetailService = customUerDetailService;
    }

    public String createAccessToken(String email) {

        Map<String, Object> headers = new HashMap<>();
        headers.put("type", "token");

        Map<String, Object> payloads = new HashMap<>();
        payloads.put("email", email);

        Date expiration = new Date();
        expiration.setTime(expiration.getTime() + accessExpiredTime);

        String jwt = Jwts.builder()
                .setHeader(headers)
                .setClaims(payloads)
                .setSubject("allinone")
                .setExpiration(expiration)
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();

        return jwt;
    }

    public Map<String, String> createRefreshToken(String email) {

        Map<String, Object> headers = new HashMap<>();
        headers.put("type", "token");

        Map<String, Object> payloads = new HashMap<>();
        payloads.put("email", email);

        Date expiration = new Date();
        expiration.setTime(expiration.getTime() + refreshExpiredTime);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
        String refreshTokenExpirationAt = simpleDateFormat.format(expiration);

        String jwt = Jwts.builder()
                .setHeader(headers)
                .setClaims(payloads)
                .setSubject("allinone")
                .setExpiration(expiration)
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();

        Map<String, String> result = new HashMap<>();
        result.put("refreshToken", jwt);
        result.put("refreshTokenExpirationAt", refreshTokenExpirationAt);

        return result;
    }

    public Authentication getAuthentication(String token) {//인증

        UserDetails userDetails = customUerDetailService.loadUserByUsername(this.getUserInfo(token));//기본적으로 제공한 details 세팅

        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    public String getUserInfo(String token) {
        return (String)Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().get("email");
    }

    public String resolveToken(HttpServletRequest request) {

        String header = request.getHeader("Authorization");
        String result = null;

        if (header != null && header.length() >= 7) {
            String is_token = header.substring(0, 7);
            if (is_token.equals("Bearer "))
                result = header.substring(7);
        }

        return result;
    }

    public JWTEnum validateJwtToken(ServletRequest request, String authToken) {

        try {
            Jwts.parser().setSigningKey(secretKey).parseClaimsJws(authToken);
            return JWTEnum.VALID;
        } catch (SignatureException exception) {
            request.setAttribute("exception", "SignatureException"); // JWT의 기존 서명을 확인하지 못했을 경우
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
}
