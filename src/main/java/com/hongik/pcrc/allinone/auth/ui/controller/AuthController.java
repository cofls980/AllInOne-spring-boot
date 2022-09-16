package com.hongik.pcrc.allinone.auth.ui.controller;

import com.hongik.pcrc.allinone.auth.application.service.AuthOperationUseCase;
import com.hongik.pcrc.allinone.auth.application.service.AuthReadUseCase;
import com.hongik.pcrc.allinone.auth.ui.requestBody.AuthCreateRequest;
import com.hongik.pcrc.allinone.auth.ui.requestBody.AuthSignInRequest;
import com.hongik.pcrc.allinone.auth.ui.view.Auth.AuthView;
import com.hongik.pcrc.allinone.exception.view.SuccessView;
import com.hongik.pcrc.allinone.exception.AllInOneException;
import com.hongik.pcrc.allinone.exception.MessageType;
import com.hongik.pcrc.allinone.exception.view.ApiResponseView;
import com.hongik.pcrc.allinone.security.jwt.JwtProvider;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/v2/users")
@Api(tags = {"User API"})
public class AuthController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final AuthOperationUseCase authOperationUseCase;
    private final AuthReadUseCase authReadUseCase;
    private final JwtProvider jwtProvider;

    @Autowired
    public AuthController(AuthOperationUseCase authOperationUseCase, AuthReadUseCase authReadUseCase, JwtProvider jwtProvider) {
        this.authOperationUseCase = authOperationUseCase;
        this.authReadUseCase = authReadUseCase;
        this.jwtProvider = jwtProvider;
    }

    //sign up
    @PostMapping("/signup")
    @ApiOperation(value = "회원가입")
    public ResponseEntity<ApiResponseView<SuccessView>> createAuth(@RequestBody AuthCreateRequest request) {

        logger.info("회원가입");
        if (ObjectUtils.isEmpty(request)) {
            throw new AllInOneException(MessageType.BAD_REQUEST);
        }

        var command = AuthOperationUseCase.AuthCreatedCommand.builder()
                .email(request.getEmail())
                .password(request.getPassword())
                .name(request.getName())
                .birth(request.getBirth())
                .gender(request.getGender())
                .phone_number(request.getPhoneNumber())
                .build();

        authOperationUseCase.createAuth(command);

        return ResponseEntity.ok(new ApiResponseView<>(new SuccessView("true")));
    }

    //sign in
    @PostMapping("/login")
    @ApiOperation(value = "로그인")
    public ResponseEntity<ApiResponseView<AuthView>> loginAuth(@RequestBody AuthSignInRequest request) throws Exception {

        logger.info("로그인");
        if (ObjectUtils.isEmpty(request)) {
            throw new AllInOneException(MessageType.BAD_REQUEST);
        }

        AuthReadUseCase.AuthFindQuery command = new AuthReadUseCase.AuthFindQuery(request.getEmail(), request.getPassword());
        AuthReadUseCase.FindAuthResult result = authReadUseCase.getAuth(command);

        //jwt 토큰 생성
        String accessToken = jwtProvider.createAccessToken(result.getEmail());
        //디비 저장
        String refreshToken = jwtProvider.createRefreshToken(result.getEmail()).get("refreshToken");
        authOperationUseCase.updateRefreshToken(result.getId(), refreshToken);

        return ResponseEntity.ok(new ApiResponseView<>(new AuthView(result, accessToken, refreshToken)));
    }

    //reset pasword
    @PutMapping("")
    @ApiOperation(value = "비밀번호 재설정")
    public ResponseEntity<ApiResponseView<SuccessView>> resetPassword(@RequestBody AuthSignInRequest request) {

        logger.info("비밀번호 재설정");
        if (ObjectUtils.isEmpty(request)) {
            throw new AllInOneException(MessageType.BAD_REQUEST);
        }

        var command = AuthOperationUseCase.AuthUpdateCommand.builder()
                .email(request.getEmail())
                .password(request.getPassword())
                .build();

        authOperationUseCase.updateAuth(command);

        return ResponseEntity.ok(new ApiResponseView<>(new SuccessView("true")));
    }

    /*PostMapping("/logout")
    public ResponseEntity<ApiResponseView<SuccessView>> logout() {// 토큰을 가지고 있어야함.
        //change refresh token value to null in db
        return ResponseEntity.ok(new ApiResponseView<>(new SuccessView("true")));
    }*/

    @DeleteMapping("")
    @ApiOperation(value = "탈퇴")
    public ResponseEntity<ApiResponseView<SuccessView>> userDelete() {

        logger.info("탈퇴");
        authOperationUseCase.deleteAuth();

        return ResponseEntity.ok(new ApiResponseView<>(new SuccessView("true")));
    }
}
