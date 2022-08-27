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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping(value = "/v2/users")
public class AuthController {

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
    public ResponseEntity<ApiResponseView<SuccessView>> createAuth(@RequestBody AuthCreateRequest request) {

        if (ObjectUtils.isEmpty(request)) {
            throw new AllInOneException(MessageType.BAD_REQUEST);
        }

        var command = AuthOperationUseCase.AuthCreatedCommand.builder()
                .id(request.getId())
                .password(request.getPassword())
                .name(request.getName())
                .birth(request.getBirth())
                .gender(request.getGender())
                .phoneNumber(request.getPhoneNumber())
                .build();

        var result = authOperationUseCase.createAuth(command);

        if (result.getId().equals("conflict")) {
            throw new AllInOneException(MessageType.CONFLICT);
        }

        return ResponseEntity.ok(new ApiResponseView<>(new SuccessView("true")));
    }

    //sign in
    @PostMapping("/login")
    public ResponseEntity<ApiResponseView<AuthView>> loginAuth(@RequestBody AuthSignInRequest request) throws Exception {

        if (ObjectUtils.isEmpty(request)) {
            throw new AllInOneException(MessageType.BAD_REQUEST);
        }

        AuthReadUseCase.AuthFindQuery command = new AuthReadUseCase.AuthFindQuery(request.getId(), request.getPassword());
        AuthReadUseCase.FindAuthResult result = authReadUseCase.getAuth(command);
        if (result == null) {
            throw new AllInOneException(MessageType.NOT_FOUND);
        }

        //jwt 토큰 생성
        String accessToken = jwtProvider.createAccessToken(result.getId());
        //디비 저장
        String refreshToken = jwtProvider.createRefreshToken(result.getId()).get("refreshToken");
        authOperationUseCase.updateRefreshToken(result.getId(), refreshToken);
        System.out.println("refresh: " + refreshToken);

        return ResponseEntity.ok(new ApiResponseView<>(new AuthView(result, accessToken, refreshToken)));
    }

    //reset pasword
    @PutMapping("")
    public ResponseEntity<ApiResponseView<SuccessView>> resetPassword(@RequestBody AuthSignInRequest request) {

        if (ObjectUtils.isEmpty(request)) {
            throw new AllInOneException(MessageType.BAD_REQUEST);
        }

        var command = AuthOperationUseCase.AuthUpdateCommand.builder()
                .id(request.getId())
                .password(request.getPassword())
                .build();

        var result = authOperationUseCase.updateAuth(command);
        if (result.equals("conflict")) {
            throw new AllInOneException(MessageType.CONFLICT);
        } else if (result.equals("not_found")) {
            throw new AllInOneException(MessageType.NOT_FOUND);
        }

        return ResponseEntity.ok(new ApiResponseView<>(new SuccessView("true")));
    }

    /*PostMapping("/logout")
    public ResponseEntity<ApiResponseView<SuccessView>> logout() {// 토큰을 가지고 있어야함.
        //change refresh token value to null in db
        return ResponseEntity.ok(new ApiResponseView<>(new SuccessView("true")));
    }*/

    @DeleteMapping("")
    public ResponseEntity<ApiResponseView<SuccessView>> userDelete() {
        authOperationUseCase.deleteAuth();

        return ResponseEntity.ok(new ApiResponseView<>(new SuccessView("true")));
    }
}
