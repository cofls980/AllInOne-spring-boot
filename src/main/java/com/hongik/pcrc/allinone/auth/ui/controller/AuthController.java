package com.hongik.pcrc.allinone.auth.ui.controller;

import com.hongik.pcrc.allinone.auth.application.security.AuthDetailsService;
import com.hongik.pcrc.allinone.auth.application.security.JwtTokenProvider;
import com.hongik.pcrc.allinone.auth.application.service.AuthOperationUseCase;
import com.hongik.pcrc.allinone.auth.application.service.AuthReadUseCase;
import com.hongik.pcrc.allinone.auth.ui.requestBody.AuthCreateRequest;
import com.hongik.pcrc.allinone.auth.ui.requestBody.AuthSignInRequest;
import com.hongik.pcrc.allinone.auth.ui.view.Auth.AuthView;
import com.hongik.pcrc.allinone.auth.ui.view.Auth.SuccessView;
import com.hongik.pcrc.allinone.exception.AllInOneException;
import com.hongik.pcrc.allinone.exception.MessageType;
import com.hongik.pcrc.allinone.auth.ui.view.ApiResponseView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/v1/auth")
public class AuthController {

    private final AuthOperationUseCase authOperationUseCase;
    private final AuthReadUseCase authReadUseCase;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthDetailsService authDetailsService;

    @Autowired
    public AuthController(AuthOperationUseCase authOperationUseCase, AuthReadUseCase authReadUseCase, JwtTokenProvider jwtTokenProvider, AuthDetailsService authDetailsService) {
        this.authOperationUseCase = authOperationUseCase;
        this.authReadUseCase = authReadUseCase;
        this.jwtTokenProvider = jwtTokenProvider;
        this.authDetailsService = authDetailsService;
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
    public ResponseEntity<ApiResponseView<AuthView>> signInAuth(@RequestBody AuthSignInRequest request) throws Exception {

        if (ObjectUtils.isEmpty(request)) {
            throw new AllInOneException(MessageType.BAD_REQUEST);
        }

        AuthReadUseCase.AuthFindQuery command = new AuthReadUseCase.AuthFindQuery(request.getId(), request.getPassword());
        AuthReadUseCase.FindAuthResult result = authReadUseCase.getAuth(command);
        if (result == null) {
            throw new AllInOneException(MessageType.NOT_FOUND);
        }
        //jwt 토큰 생성 필요
        /*UserDetails userDetails = authDetailsService.loadUserByUsername(result.getId());
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                result.getId(), request.getPassword(), userDetails.getAuthorities());*/

        String accessToken = jwtTokenProvider.createAccessToken(result.getId());
        String refreshToken = jwtTokenProvider.issueRefreshToken(result.getId());

        return ResponseEntity.ok(new ApiResponseView<>(new AuthView(result, accessToken, refreshToken)));
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
