package com.hongik.pcrc.allinone.auth.ui.controller;

import com.hongik.pcrc.allinone.auth.application.service.EmailService;
import com.hongik.pcrc.allinone.auth.ui.requestBody.EmailSendCodeRequest;
import com.hongik.pcrc.allinone.auth.ui.requestBody.EmailVerifyRequest;
import com.hongik.pcrc.allinone.exception.view.SuccessView;
import com.hongik.pcrc.allinone.exception.AllInOneException;
import com.hongik.pcrc.allinone.exception.MessageType;
import com.hongik.pcrc.allinone.exception.view.ApiResponseView;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.mail.MessagingException;
import javax.validation.Valid;

@RestController
@RequestMapping(value = "/v2/email")
@Api(tags = {"Email API"})
public class EmailController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final EmailService emailService;

    public EmailController(EmailService emailService) {
        this.emailService = emailService;
    }

    /** 이메일 인증 코드 보내기*/
    @PostMapping("")
    @ApiOperation(value = "[회원가입] 이메일로 인증 코드 전송")
    public ResponseEntity<ApiResponseView<SuccessView>> emailAuth(@RequestBody EmailSendCodeRequest request) throws MessagingException {

        logger.info("[회원가입] 이메일로 인증 코드 전송");
        if (ObjectUtils.isEmpty(request)) {
            throw new AllInOneException(MessageType.BAD_REQUEST);
        }
        emailService.sendMessage(request.getEmail());

        return ResponseEntity.ok(new ApiResponseView<>(new SuccessView("true")));
    }

    /** 이메일 인증 코드 검증*/
    @PostMapping("/confirm")
    @ApiOperation(value = "인증 코드 확인")
    public ResponseEntity<ApiResponseView<SuccessView>> verifyCode(@Valid @RequestBody EmailVerifyRequest request) {

        logger.info("인증 코드 확인");
        if (ObjectUtils.isEmpty(request)) {
            throw new AllInOneException(MessageType.BAD_REQUEST);
        }
        String result = emailService.verifyCode(request.getEmail(), request.getCode());
        if (result.equals("false")) {
            throw new AllInOneException(MessageType.NOT_FOUND);
        }

        return ResponseEntity.ok(new ApiResponseView<>(new SuccessView(result)));
    }

    @PostMapping("/reset-pwd")
    @ApiOperation(value = "[비밀번호 재설정] 이메일로 인증 코드 전송")
    public ResponseEntity<ApiResponseView<SuccessView>> emailPasswordAuth(@RequestBody EmailSendCodeRequest request) throws MessagingException {

        logger.info("[비밀번호 재설정] 이메일로 인증 코드 전송");
        if (ObjectUtils.isEmpty(request)) {
            throw new AllInOneException(MessageType.BAD_REQUEST);
        }
        var result = emailService.sendMessageExist(request.getEmail());
        if (result.equals("not_found")) {
            throw new AllInOneException(MessageType.NOT_FOUND);
        }

        return ResponseEntity.ok(new ApiResponseView<>(new SuccessView(result)));
    }
}
