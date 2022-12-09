package com.hongik.pcrc.allinone.auth.ui.controller;

import com.hongik.pcrc.allinone.auth.application.service.AuthOperationUseCase;
import com.hongik.pcrc.allinone.auth.application.service.AuthReadUseCase;
import com.hongik.pcrc.allinone.auth.ui.requestBody.AuthCreateRequest;
import com.hongik.pcrc.allinone.auth.ui.requestBody.AuthSignInRequest;
import com.hongik.pcrc.allinone.auth.ui.requestBody.FriendCreateRequest;
import com.hongik.pcrc.allinone.auth.ui.view.AuthView;
import com.hongik.pcrc.allinone.exception.view.SuccessView;
import com.hongik.pcrc.allinone.exception.AllInOneException;
import com.hongik.pcrc.allinone.exception.MessageType;
import com.hongik.pcrc.allinone.exception.view.ApiResponseView;
import com.hongik.pcrc.allinone.security.jwt.JwtProvider;
import com.hongik.pcrc.allinone.security.jwt.TokenInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

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

    @PostMapping(value = "/signup", produces = "application/json")
    @ApiOperation(value = "회원가입")
    public ResponseEntity<ApiResponseView<SuccessView>> createAuth(@Valid @RequestBody AuthCreateRequest request) {

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

    //권한(role) 구분 없음
    @PostMapping(value = "/login", produces = "application/json")
    @ApiOperation(value = "로그인")
    public ResponseEntity<ApiResponseView<AuthView>> loginAuth(@Valid @RequestBody AuthSignInRequest request) throws Exception {

        logger.info("로그인");
        if (ObjectUtils.isEmpty(request)) {
            throw new AllInOneException(MessageType.BAD_REQUEST);
        }

        AuthReadUseCase.AuthFindQuery command = new AuthReadUseCase.AuthFindQuery(request.getEmail(), request.getPassword());
        AuthReadUseCase.FindAuthResult result = authReadUseCase.getAuth(command);

        //jwt 토큰 생성
        TokenInfo tokenInfo = jwtProvider.generateToken(request.getEmail());
        authOperationUseCase.updateRefreshToken(result.getId(), tokenInfo.getRefreshToken());

        return ResponseEntity.ok(new ApiResponseView<>(new AuthView(result, tokenInfo.getAccessToken(), tokenInfo.getRefreshToken())));
    }

    @PutMapping(value = "", produces = "application/json")
    @ApiOperation(value = "비밀번호 재설정")
    public ResponseEntity<ApiResponseView<SuccessView>> resetPassword(@Valid @RequestBody AuthSignInRequest request) {

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

    @DeleteMapping(value = "", produces = "application/json")
    @ApiOperation(value = "탈퇴")
    public ResponseEntity<ApiResponseView<SuccessView>> userDelete() {

        logger.info("탈퇴");
        authOperationUseCase.deleteAuth();

        return ResponseEntity.ok(new ApiResponseView<>(new SuccessView("true")));
    }

    @GetMapping(value = "/myfriends", produces = "application/json")
    @ApiOperation(value = "내 친구 리스트 보기")
    public ResponseEntity<List<AuthReadUseCase.FindMyFriendResult>> getMyFriendList() {
        logger.info("내 친구 리스트 보기");

        var result = authReadUseCase.getMyFriendList();
        if (result == null || result.isEmpty()) {
            throw new AllInOneException(MessageType.NOT_FOUND);
        }

        return ResponseEntity.ok(result);
    }

    @PostMapping(value = "/myfriends", produces = "application/json")
    @ApiOperation(value = "친구 추가")
    public ResponseEntity<ApiResponseView<SuccessView>> addFriend(@RequestBody FriendCreateRequest request) {
        logger.info("친구 추가");
        if (ObjectUtils.isEmpty(request)) {
            throw new AllInOneException(MessageType.BAD_REQUEST);
        }

        var command = AuthOperationUseCase.FriendCreatedCommand.builder()
                .email(request.getUser_email())
                .name(request.getUser_name())
                .build();

        authOperationUseCase.addFriend(command);

        return ResponseEntity.ok(new ApiResponseView<>(new SuccessView("true")));
    }

    @DeleteMapping(value = "/myfriends/{friend_id}", produces = "application/json")
    @ApiOperation(value = "친구 삭제")
    public ResponseEntity<ApiResponseView<SuccessView>> deleteFriend(@PathVariable int friend_id) {
        logger.info("친구 삭제");

        authOperationUseCase.deleteFriend(friend_id);

        return ResponseEntity.ok(new ApiResponseView<>(new SuccessView("true")));
    }
}
