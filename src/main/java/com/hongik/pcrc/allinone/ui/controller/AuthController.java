package com.hongik.pcrc.allinone.ui.controller;

import com.hongik.pcrc.allinone.application.service.AuthOperationUseCase;
import com.hongik.pcrc.allinone.application.service.AuthReadUseCase;
import com.hongik.pcrc.allinone.exception.AllInOneException;
import com.hongik.pcrc.allinone.exception.MessageType;
import com.hongik.pcrc.allinone.ui.requestBody.AuthCreateRequest;
import com.hongik.pcrc.allinone.ui.view.ApiResponseView;
import com.hongik.pcrc.allinone.ui.view.Auth.AuthView;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/v1/auth")
public class AuthController {

    private final AuthOperationUseCase authOperationUseCase;
    private final AuthReadUseCase authReadUseCase;

    public AuthController(AuthOperationUseCase authOperationUseCase, AuthReadUseCase authReadUseCase) {
        this.authOperationUseCase = authOperationUseCase;
        this.authReadUseCase = authReadUseCase;
    }

    @PostMapping("")
    public ResponseEntity<ApiResponseView<AuthView>> createAuth(@RequestBody AuthCreateRequest request) {

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

        return ResponseEntity.ok(new ApiResponseView<>(new AuthView(result)));
    }
}
