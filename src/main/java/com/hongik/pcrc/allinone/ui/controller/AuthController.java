package com.hongik.pcrc.allinone.ui.controller;

import com.hongik.pcrc.allinone.application.Authentication.JwtTokenProvider;
import com.hongik.pcrc.allinone.application.service.AuthOperationUseCase;
import com.hongik.pcrc.allinone.application.service.AuthReadUseCase;
import com.hongik.pcrc.allinone.exception.AllInOneException;
import com.hongik.pcrc.allinone.exception.MessageType;
import com.hongik.pcrc.allinone.ui.requestBody.AuthCreateRequest;
import com.hongik.pcrc.allinone.ui.requestBody.AuthSignInRequest;
import com.hongik.pcrc.allinone.ui.view.ApiResponseView;
import com.hongik.pcrc.allinone.ui.view.Auth.AuthView;
import com.hongik.pcrc.allinone.ui.view.Auth.SuccessView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping(value = "/v1/auth")
public class AuthController {

    private final AuthOperationUseCase authOperationUseCase;
    private final AuthReadUseCase authReadUseCase;
    private final JwtTokenProvider jwtTokenProvider;

    @Autowired
    public AuthController(AuthOperationUseCase authOperationUseCase, AuthReadUseCase authReadUseCase, JwtTokenProvider jwtTokenProvider) {
        this.authOperationUseCase = authOperationUseCase;
        this.authReadUseCase = authReadUseCase;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    //sign up
    @PostMapping("")
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
    @PostMapping("/signin")
    public ResponseEntity<ApiResponseView<AuthView>> signInAuth(@RequestBody AuthSignInRequest request, HttpServletRequest httpServletRequest) throws Exception {

        if (ObjectUtils.isEmpty(request)) {
            throw new AllInOneException(MessageType.BAD_REQUEST);
        }

        AuthReadUseCase.AuthFindQuery command = new AuthReadUseCase.AuthFindQuery(request.getId(), request.getPassword());
        AuthReadUseCase.FindAuthResult result = authReadUseCase.getAuth(command);
        if (result == null) {
            throw new AllInOneException(MessageType.NOT_FOUND);
        }
        //jwt 토큰 생성 필요
        String token = jwtTokenProvider.generateToken(result.getId());

        return ResponseEntity.ok(new ApiResponseView<>(new AuthView(result, token)));
    }

    //reset pasword
    @PostMapping("/pwd")
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

}
