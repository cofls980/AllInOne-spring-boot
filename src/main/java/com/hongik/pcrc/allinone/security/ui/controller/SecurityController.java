package com.hongik.pcrc.allinone.security.ui.controller;

import com.hongik.pcrc.allinone.auth.application.service.AuthOperationUseCase;
import com.hongik.pcrc.allinone.exception.AllInOneException;
import com.hongik.pcrc.allinone.exception.MessageType;
import com.hongik.pcrc.allinone.exception.view.ApiResponseView;
import com.hongik.pcrc.allinone.security.jwt.JWTEnum;
import com.hongik.pcrc.allinone.security.jwt.JwtProvider;
import com.hongik.pcrc.allinone.security.jwt.TokenInfo;
import com.hongik.pcrc.allinone.security.service.SecurityService;
import com.hongik.pcrc.allinone.security.ui.view.AccessTokenView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping(value = "/v2/security")
public class SecurityController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final JwtProvider jwtProvider;
    private final SecurityService securityService;

    public SecurityController(JwtProvider jwtProvider, SecurityService securityService) {
        this.jwtProvider = jwtProvider;
        this.securityService = securityService;
    }

    //만약 access 토큰을 사용하여 요청이 들어왔을 때 만료된 토큰이라는 메시지를 받으면
    //refresh 토큰을 사용하여 재발급 요청 만약 만료된 토큰이라는 메시지를 받으면 재로그인 필수
    //그게 아니라면 서버에서 재발급 토큰이 서버에 저장되어 있는 토큰과 비교하여 유효한 토큰인지 확인
    //이때 유효한 토큰이라면 access 토큰을 새로 만들어서 응답보냄
    //유효하지 않다면 "재로그인 필요" 메시지 보냄.
    @GetMapping("/reissue")
    public ResponseEntity<ApiResponseView<AccessTokenView>> createAuth(HttpServletRequest request) {

        logger.info("토큰 재발급");
        String token = jwtProvider.resolveToken(request);

        //refresh token 같은지 확인
        if (jwtProvider.validateJwtToken(request, token) == JWTEnum.INVLID || !securityService.validateRefreshToken(token)) {
            System.out.println("invalid");
            throw new AllInOneException(MessageType.ReLogin);
        }

        //같으면 새로 access token 발급
        TokenInfo tokenInfo = securityService.createNewAccessToken(token);

        return ResponseEntity.ok(new ApiResponseView<>(new AccessTokenView(tokenInfo.getAccessToken())));
    }
}
